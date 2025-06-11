package ir.stts.bajet.elastic.index;

import java.util.List;

public interface IndexService {

    String WILDCARD = "*";

    String getIndex(Long time);

    List<String> getIndexOf();

    List<String> getIndexOf(Long from, Long to);
}