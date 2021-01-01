package revolver.headead.aifa.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DrugAuthorizationAdapter extends TypeAdapter<Boolean> {

    @Override
    public Boolean read(JsonReader in) throws IOException {
        final boolean authorized;
        in.beginArray();
        authorized = "A".equals(in.nextString());
        in.endArray();
        return authorized;
    }

    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        // no-op
    }
}
