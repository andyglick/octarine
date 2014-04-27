package com.codepoetics.octarine.records;

import com.codepoetics.octarine.matching.Extractor;
import org.pcollections.PMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Schema<T> extends BiConsumer<Record, Consumer<String>>, Extractor<Record, Valid<T>> {

    @Override default boolean test(Record record) {
        return validate(record).isValid();
    }

    @Override default Valid<T> extract(Record record) {
        return validate(record).get();
    }

    default Validation<T> validate(Value...values) {
        return validate(Record.of(values));
    }

    default Validation<T> validate(Record record) {
        List<String> validationErrors = new LinkedList<>();
        accept(record, validationErrors::add);
        if (validationErrors.isEmpty()) {
            return Validation.valid(new Valid<T>() {
                @Override
                public <S> Optional<S> get(Key<S> key) {
                    return record.get(key);
                }

                @Override
                public PMap<Key<?>, Object> values() {
                    return record.values();
                }

                @Override public int hashCode() { return record.hashCode(); }
                @Override public boolean equals(Object other) { return record.equals(other); }

                @Override
                public Schema<T> schema() {
                    return Schema.this;
                }
            });
        }
        return Validation.invalid(validationErrors);
    }
}