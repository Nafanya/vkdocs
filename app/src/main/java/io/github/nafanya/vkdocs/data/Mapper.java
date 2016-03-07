package io.github.nafanya.vkdocs.data;

import java.util.ArrayList;
import java.util.List;

//Mapper from db model to domain model
public abstract class Mapper<From, To> {
    public abstract To transform(From x);

    public List<To> transform(List<From> list) {
        List<To> ret = new ArrayList<>();
        for (From x : list)
            ret.add(transform(x));
        return ret;
    }

    public List<From> transformInv(List<To> list) {
        List<From> ret = new ArrayList<>();
        for (To x : list)
            ret.add(transformInv(x));
        return ret;
    }

    public abstract From transformInv(To x);
}
