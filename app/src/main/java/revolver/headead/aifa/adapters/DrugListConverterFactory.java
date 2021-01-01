package revolver.headead.aifa.adapters;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import revolver.headead.aifa.model.Drug;

public class DrugListConverterFactory extends Converter.Factory {

    private static final Gson gson = new Gson();

    @Nullable
    @Override
    public Converter<ResponseBody, List<Drug>> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (((ParameterizedType) type).getRawType() != List.class || ((ParameterizedType) type).getActualTypeArguments()[0] != Drug.class) {
            return null;
        }
        return new Converter<ResponseBody, List<Drug>>() {
            @Nullable
            @Override
            public List<Drug> convert(@NonNull ResponseBody value) throws IOException {
                return clearDuplicates(gson.fromJson(JsonParser.parseString(value.string())
                        .getAsJsonObject()
                        .get("response")
                        .getAsJsonObject()
                        .get("docs")
                        .getAsJsonArray(), new TypeToken<List<Drug>>(){}.getType()));
            }
        };
    }

    private static List<Drug> clearDuplicates(final List<Drug> input) {
        final Set<String> seen = new HashSet<>();
        for (int j = input.size() - 1; j >= 0; j--) {
            Drug drug = input.get(j);
            if (drug.getDrugDescription() == null || drug.getDrugDescription().isEmpty() ||
                    drug.getDrugMaker() == null || drug.getDrugMaker().isEmpty()) {
                input.remove(j);
            }
        }
        for (int i = input.size() - 1; i >= 0; i--) {
            Drug drug = input.get(i);
            if (seen.contains(drug.getDrugId())) {
                input.remove(i);
            } else {
                seen.add(drug.getDrugId());
            }
        }
        return input;
    }
}
