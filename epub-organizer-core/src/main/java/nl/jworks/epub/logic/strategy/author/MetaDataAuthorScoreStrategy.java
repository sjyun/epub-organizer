package nl.jworks.epub.logic.strategy.author;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import nl.jworks.epub.annotations.NotNull;
import nl.jworks.epub.domain.Author;
import nl.jworks.epub.logic.names.Name;
import nl.jworks.epub.logic.names.PersonNameCategorizer;
import nl.jworks.epub.logic.strategy.BookImportContext;
import nl.jworks.epub.logic.strategy.ScoreStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MetaDataAuthorScoreStrategy implements ScoreStrategy<AuthorScore> {

    private static Logger log = LoggerFactory.getLogger(MetaDataAuthorScoreStrategy.class);

    @NotNull
    @Override
    public AuthorScore score(BookImportContext context) {
        try {
            List<nl.siegmann.epublib.domain.Author> epubAuthors = context.getEpubBook().getMetadata().getAuthors();

            AuthorScore categorizerScore = getAuthorScoreUsingCategorizer(epubAuthors);
            AuthorScore metaDataScore = getAuthorScoreUsingPlainMetaData(epubAuthors);

            Ordering<AuthorScore> o = new Ordering<AuthorScore>() {
                @Override
                public int compare(AuthorScore left, AuthorScore right) {
                    return Doubles.compare(left.getScore(), right.getScore());
                }
            };

            return o.max(Arrays.asList(categorizerScore, metaDataScore));
        } catch (Exception e) {
            log.error("Could not determine score for {}", context);

            return new AuthorScore(Collections.<Author>emptyList(), MetaDataAuthorScoreStrategy.class);
        }
    }

    private AuthorScore getAuthorScoreUsingCategorizer(List<nl.siegmann.epublib.domain.Author> epubAuthors) {
        List<Author> authors = new ArrayList<>();

        for (nl.siegmann.epublib.domain.Author epubAuthor : epubAuthors) {
            Name name = new PersonNameCategorizer().categorize(new String[]{epubAuthor.getFirstname(), epubAuthor.getLastname()});

            authors.add(new Author(name.getFirstName(), name.getLastName()));
        }
        return new AuthorScore(authors, MetaDataAuthorScoreStrategy.class);
    }

    private AuthorScore getAuthorScoreUsingPlainMetaData(List<nl.siegmann.epublib.domain.Author> epubAuthors) {
        List<Author> authors = new ArrayList<>();

        for (nl.siegmann.epublib.domain.Author epubAuthor : epubAuthors) {

            authors.add(new Author(epubAuthor.getFirstname(), epubAuthor.getLastname()));
        }
        return new AuthorScore(authors, MetaDataAuthorScoreStrategy.class);
    }
}
