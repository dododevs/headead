package revolver.headead.aifa.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DrugArrayToStringAdapter extends TypeAdapter<String> {

    @Override
    public String read(JsonReader in) throws IOException {
        final String inner;
        in.beginArray();
        inner = in.nextString();
        in.endArray();
        return inner;
    }

    @Override
    public void write(JsonWriter out, String value) throws IOException {
        // no-op
    }
}
