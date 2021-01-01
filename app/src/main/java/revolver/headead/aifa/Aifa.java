package revolver.headead.aifa;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import revolver.headead.aifa.adapters.DrugListConverterFactory;
import revolver.headead.aifa.adapters.DrugPackagingFormatsListConverterFactory;
import revolver.headead.aifa.services.DrugLookupService;

public final class Aifa {

    private static final Retrofit retrofit;
    private static final DrugLookupService drugLookupService;

    static {
        retrofit = new Retrofit.Builder().baseUrl("https://www.agenziafarmaco.gov.it")
                .addConverterFactory(new DrugListConverterFactory())
                .addConverterFactory(new DrugPackagingFormatsListConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(chain -> {
                    final String query = chain.request().url().queryParameter("q");
                    if (query != null && query.contains("sm_field_descrizione_farmaco")) {
                        final HttpUrl url = chain.request().url().newBuilder()
                                .addQueryParameter("fl", "sm_field_codice_farmaco,sm_field_descrizione_farmaco,sm_field_descrizione_ditta")
                                .addQueryParameter("df", "sm_field_descrizione_farmaco")
                                .addQueryParameter("wt", "json")
                                .addQueryParameter("rows", "150000")
                                .build();
                        Log.d("url", url.toString());
                        return chain.proceed(chain.request().newBuilder().url(
                                url
                        ).build());
                    } else if (query != null && query.contains("sm_field_codice_farmaco")) {
                        return chain.proceed(chain.request().newBuilder().url(
                                chain.request().url().newBuilder()
                                        .addQueryParameter("df", "sm_field_codice_farmaco")
                                        .addQueryParameter("wt", "json")
                                        .addQueryParameter("rows", "150000")
                                        .build()
                        ).build());
                    } else if (query != null && query.contains("sm_field_chiave_confezione")) {
                        return chain.proceed(chain.request().newBuilder().url(
                                chain.request().url().newBuilder()
                                        .addQueryParameter("df", "sm_field_chiave_confezione")
                                        .addQueryParameter("wt", "json")
                                        .addQueryParameter("rows", "150000")
                                        .build()
                        ).build());
                    }
                    return chain.proceed(chain.request());
                }).build()).build();
        drugLookupService = retrofit.create(DrugLookupService.class);
    }

    public static DrugLookupService getDrugLookupService() {
        return drugLookupService;
    }

    public static String buildDrugQueryString(final String query) {
        return "sm_field_descrizione_farmaco:" + query + "*";
    }

    public static String buildDrugPackagingQueryString(final String drugId) {
        return "sm_field_codice_farmaco:" + drugId;
    }

    public static String buildDrugPackagingByIdQueryString(final String id) {
        return "sm_field_chiave_confezione:" + id;
    }
}
