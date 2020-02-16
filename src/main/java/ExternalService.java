import retrofit2.Call;
import retrofit2.http.POST;

public interface ExternalService {

    @POST("/item")
    Call<Void> postItem();

}
