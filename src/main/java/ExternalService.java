import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.POST;

public interface ExternalService {

    static ExternalService create(String url) {
        var retrofit = new Retrofit.Builder().baseUrl(url).build();
        return retrofit.create(ExternalService.class);
    }

    @POST("/item")
    Call<Void> postItem();

}
