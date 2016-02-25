package io.github.nafanya.vkdocs.data;

import java.util.List;

//Mapper from db model to domain model
public interface Mapper<From, To> {
    To transform(From x);
    List<To> transform(List<From> x);

    From transformInv(To x);
}
