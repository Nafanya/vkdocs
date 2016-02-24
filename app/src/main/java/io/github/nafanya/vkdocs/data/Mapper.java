package io.github.nafanya.vkdocs.data;

import java.util.List;


public interface Mapper<F, T> {
    T transform(F vkDoc);
    List<T> transform(List<F> vkDoc);

    F transformInv(T vkApiDoc);
}
