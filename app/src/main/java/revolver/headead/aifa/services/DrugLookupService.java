package revolver.headead.aifa.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import revolver.headead.aifa.model.Drug;
import revolver.headead.aifa.model.DrugPackaging;

public interface DrugLookupService {

    @GET("services/search/select")
    Call<List<Drug>> search(@Query("q") String query);

    @GET("services/search/select")
    Call<List<DrugPackaging>> findPackagingFormats(@Query("q") String query);

    @GET("services/search/select")
    Call<List<DrugPackaging>> findPackagingFormatById(@Query("q") String query);
}
