package de.kreth.clubhelper.personedit.remote;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import de.kreth.clubhelper.personedit.data.Gender;

@JsonComponent
public class GenderSerializer extends JsonSerializer<Gender> {

    @Override
    public void serialize(Gender value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
	gen.writeNumber(value.getId());
    }

}
