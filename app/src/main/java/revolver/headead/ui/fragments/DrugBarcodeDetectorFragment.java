package revolver.headead.ui.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import revolver.headead.R;
import revolver.headead.ui.activities.DrugLookupActivity;
import revolver.headead.util.logic.Conditions;
import revolver.headead.util.misc.Base32;
import revolver.headead.util.ui.M;

public class DrugBarcodeDetectorFragment extends DialogFragment {

    private static final int REQUEST_CAMERA_PERMISSION = "lemmeSeeYou".hashCode() & 0xff;

    private CameraDevice activeCamera;
    private CameraCaptureSession activeSession;
    private boolean captureInProgress = false;

    private ImageReader buffer;
    private BarcodeScanner barcodeScanner = BarcodeScanning.getClient(
            new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_CODE_39)
                    .build());
    private boolean barcodeFound = false;

    private SurfaceView surfaceView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), R.style.RoundedAdaptiveDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drug_barcode_detector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        surfaceView = view.findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                checkCameraPermission();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("surfaceChanged", "width: " + width + " height: " + height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.CAMERA }, REQUEST_CAMERA_PERMISSION);
        } else {
            initializeCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    initializeCamera();
                } catch (SecurityException e) {
                    /* this should not happen */
                    dismiss();
                }
            } else {
                Toast.makeText(requireContext(),
                        R.string.fragment_drug_barcode_detector_camera_permission_denied,
                            Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void initializeCamera() {
        final String id;
        try {
            id = getRearCameraIdOrThrow();

            /* get all the possible configurations for the selected device */
            final StreamConfigurationMap map = getCameraManager().getCameraCharacteristics(id)
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Objects.requireNonNull(map);

            /* get all available sizes compatible with SurfaceView */
            final Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
            Objects.requireNonNull(sizes);
            Conditions.checkFalse(sizes.length == 0);

            /* find the first size to have a smaller width than the device's screen */
            final Size size = getSuitableSizeForScreen(sizes);

            /* setup the surface to be correctly sized before attempting to open the camera */
            surfaceView.getHolder().setFixedSize(size.getWidth(), size.getHeight());
            surfaceView.getHolder().setFormat(PixelFormat.RGB_565);

            /* setup the image buffer to the correct size and format */
            buffer = ImageReader.newInstance(
                    size.getWidth(), size.getHeight(), ImageFormat.YUV_420_888, 16);
            buffer.setOnImageAvailableListener((ImageReader.OnImageAvailableListener) reader ->
                    onImageAvailable(), null);

            /* open the camera! */
            getCameraManager().openCamera(id, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    onCameraDeviceAvailable(camera);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    dismiss();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    Log.d("onError", "error: " + error);
                    onCameraDeviceDying();
                }
            }, null);
        } catch (Exception e) {
            Log.w("initialization", e);
            onCameraDeviceDying();
        }
    }

    private void onImageAvailable() {
        final Image image;
        try {
            image = buffer.acquireLatestImage();
        } catch (IllegalStateException e) {
            return;
        }
        if (image == null || barcodeFound) {
            return;
        }
        final InputImage in = InputImage.fromMediaImage(image, 0);
        barcodeScanner.process(in).addOnSuccessListener(barcodes -> {
            if (barcodes != null && !barcodes.isEmpty() && !barcodeFound) {
                onFarmacodeDetected(barcodes.get(0).getDisplayValue());
            }
        }).addOnCompleteListener((t) -> image.close());
    }

    private void onFarmacodeDetected(final String barcode) {
        barcodeFound = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getVibrator().vibrate(VibrationEffect
                    .createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            getVibrator().vibrate(100L);
        }

        final String farmacode = Base32.decode(barcode);
        ((DrugLookupActivity) requireActivity()).onFarmacodeDetected(farmacode);

        Log.d("farmacode", farmacode);
        dismiss();
    }

    private void onCameraDeviceAvailable(final CameraDevice device) {
        Log.d("onCameraDeviceAvailable", device.toString());
        activeCamera = device;
        try {
            activeCamera.createCaptureSession(new ArrayList<>(Arrays.asList(
                    surfaceView.getHolder().getSurface(),
                    buffer.getSurface()
            )), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    onCaptureSessionConfigured(session);
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.d("onConfigureFailed", "failed!");
                    onCameraDeviceDying();
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.w("createCaptureSession", e);
            onCameraDeviceDying();
        }
    }

    private void onCaptureSessionConfigured(final CameraCaptureSession session) {
        activeSession = session;
        try {
            final CaptureRequest.Builder builder = activeCamera
                    .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surfaceView.getHolder().getSurface());
            builder.addTarget(buffer.getSurface());
            session.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session,
                                             @NonNull CaptureRequest request,
                                             long timestamp, long frameNumber) {
                    captureInProgress = true;
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.w("capture", e);
            onCameraDeviceDying();
        }
    }

    private void onCameraDeviceDying() {
        surfaceView.getHolder().getSurface().release();
        Toast.makeText(requireContext(),
                R.string.fragment_drug_barcode_detector_camera_error,
                Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (activeCamera != null && activeSession != null && captureInProgress) {
            activeCamera.close();
            surfaceView.getHolder().getSurface().release();
        }
    }

    private CameraManager getCameraManager() {
        return (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
    }

    private Vibrator getVibrator() {
        return (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    private String getRearCameraIdOrThrow() throws CameraAccessException, IllegalStateException {
        final String[] cameras = getCameraManager().getCameraIdList();
        for (final String camera : cameras) {
            try {
                final CameraCharacteristics metadata =
                        getCameraManager().getCameraCharacteristics(camera);
                final Integer facing = metadata.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    return camera;
                }
            } catch (CameraAccessException e) {
                /* continue */
            }
        }
        throw new IllegalStateException("no back facing camera found!");
    }

    private Size getSuitableSizeForScreen(final Size[] sizes) {
        for (final Size size : sizes) {
            if (size.getWidth() <= M.screenWidth()) {
                return size;
            }
        }
        return sizes[0];
    }
}
