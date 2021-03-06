package nl.jworks.epub.logic.strategy;

import nl.jworks.epub.annotations.NotNull;

public interface ScoreStrategy<T extends Score> {

    @NotNull
    T score(BookImportContext context);
}
