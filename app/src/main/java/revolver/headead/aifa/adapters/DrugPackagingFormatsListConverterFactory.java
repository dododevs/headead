package revolver.headead.aifa.adapters;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import revolver.headead.aifa.model.Drug;
import revolver.headead.aifa.model.DrugPackaging;

public class DrugPackagingFormatsListConverterFactory extends Converter.Factory {

    private final Gson gson = new Gson();

    @Nullable
    @Override
    public Converter<ResponseBody, List<DrugPackaging>> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (((ParameterizedType) type).getRawType() != List.class || ((ParameterizedType) type).getActualTypeArguments()[0] != DrugPackaging.class) {
            return null;
        }
        return new Converter<ResponseBody, List<DrugPackaging>>() {
            @Nullable
            @Override
            public List<DrugPackaging> convert(@NonNull ResponseBody value) throws IOException {
                return normalize(gson.fromJson(JsonParser.parseString(value.string())
                        .getAsJsonObject()
                        .get("response")
                        .getAsJsonObject()
                        .get("docs")
                        .getAsJsonArray(), new TypeToken<List<DrugPackaging>>(){}.getType()));
            }
        };
    }

    /* clear invalid entries and revoked packaging formats */
    private static List<DrugPackaging> normalize(final List<DrugPackaging> input) {
        for (int j = input.size() - 1; j >= 0; j--) {
            DrugPackaging drugPackagingFormat = input.get(j);
            if (drugPackagingFormat.getPackagingDescription() == null || drugPackagingFormat.getPackagingDescription().isEmpty() ||
                    drugPackagingFormat.getActivePrinciple() == null || drugPackagingFormat.getActivePrinciple().isEmpty() ||
                        !drugPackagingFormat.isAuthorized()) {
                input.remove(j);
            }
        }
        return input;
    }
}
