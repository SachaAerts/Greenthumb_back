package com.GreenThumb.api.resources.domain.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugGenerator {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");

    /**
     * Génère un slug à partir d'un titre.
     * Ex: "Comment planter des tomates ?" -> "comment-planter-des-tomates"
     *
     * @param input Le texte à transformer en slug
     * @return Le slug généré
     */
    public static String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide");
        }

        String slug = input.toLowerCase(Locale.FRENCH);

        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", ""); // Retire les accents

        slug = WHITESPACE.matcher(slug).replaceAll("-");

        slug = NON_LATIN.matcher(slug).replaceAll("");

        slug = EDGES_DASHES.matcher(slug).replaceAll("");

        if (slug.isBlank()) {
            throw new IllegalArgumentException("Impossible de générer un slug valide à partir de : " + input);
        }

        return slug;
    }

    /**
     * Génère un slug unique en ajoutant un suffixe si nécessaire.
     * Ex: "mon-article" devient "mon-article-2" si le slug existe déjà
     *
     * @param baseSlug Le slug de base
     * @param counter Le compteur à ajouter
     * @return Le slug avec suffixe
     */
    public static String generateUniqueSlug(String baseSlug, int counter) {
        return counter == 0 ? baseSlug : baseSlug + "-" + counter;
    }
}
