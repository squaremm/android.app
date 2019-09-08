package com.square.android.data.network;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.Set;

public final class IgnoreObjectIfIncorrect implements JsonAdapter.Factory {

    @Retention(RetentionPolicy.RUNTIME)
    @JsonQualifier
    public @interface IgnoreJsonObjectError {
    }

    @Override
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        if (annotations != null && annotations.size() > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof IgnoreJsonObjectError) {
                    final JsonAdapter<Object> delegate = moshi.nextAdapter(this, type, Types.nextAnnotations(annotations, IgnoreJsonObjectError.class));
                    return new JsonAdapter<Object>() {
                        @Override
                        public Object fromJson(JsonReader reader) throws IOException {
                            JsonReader.Token peek = reader.peek();
                            if (peek != JsonReader.Token.BEGIN_OBJECT) {
                                reader.skipValue();
                                return null;
                            }
                            return delegate.fromJson(reader);
                        }

                        @Override
                        public void toJson(JsonWriter writer, Object value) throws IOException {
                            delegate.toJson(writer, value);
                        }
                    };
                }
            }
        }
        return null;
    }
}