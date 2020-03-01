package com.networknt.schema.uri;

import com.networknt.schema.uri.URIFactory;

import java.net.*;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ValidationContextAwareClasspathUriFactory implements URIFactory {

    static final URLStreamHandler STREAM_HANDLER = new ClasspathURLStreamHandler();

    public static final String[] SUPPORTED_SCHEMES = Collections.unmodifiableSet(
            ClasspathURLStreamHandler.SUPPORTED_SCHEMES).toArray(new String[]{});

    private String validationContext;

    public ValidationContextAwareClasspathUriFactory(String validationContext) {
        this.validationContext = validationContext;
    }

    public URI create(String uri) {
        String newUri = Stream.of(
                (Supplier<Optional<String>>)() -> insertSegmentAfterChar(uri, validationContext, '/'),
                () -> insertSegmentAfterChar(uri, validationContext, ':'),
                () -> Optional.of(validationContext + '/' + uri)
        ).map(Supplier::get).filter(Optional::isPresent).map(Optional::get).findFirst().orElse(uri);
        System.out.println("Original uri : " + uri);
        System.out.println("Modified uri : " + newUri);
        try {
            return new URL(null, newUri, STREAM_HANDLER).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Unable to create URI.", e);
        }
    }

    public URI create(URI baseURI, String segment) {
        String newSegment = Stream.of(
                (Supplier<Optional<String>>)() -> insertSegmentAfterChar(segment, validationContext, '/'),
                () -> Optional.of(validationContext + '/' + segment)
        ).map(Supplier::get).filter(Optional::isPresent).map(Optional::get).findFirst().orElse(segment);
        System.out.println("Original segment : " + segment);
        System.out.println("Modified segment : " + newSegment);
        try {
            return new URL(convert(baseURI), newSegment, STREAM_HANDLER).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Unable to create URI.", e);
        }
    }

    public static URL convert(final URI uri) throws MalformedURLException {
        return new URL(null, uri.toString(), STREAM_HANDLER);
    }

    private Optional<String> insertSegmentAfterChar(String source, String segment, char separator) {
        int lastSeparatorIndex = source.lastIndexOf(separator);
        if (lastSeparatorIndex >= 0) {
            String prefix = source.substring(0, lastSeparatorIndex);
            String suffix = source.substring(lastSeparatorIndex+1);
            if (prefix.endsWith(segment)) {
                return Optional.of(source);
            } else {
                return Optional.of(prefix + separator + segment + '/' + suffix);
            }
        }
        return Optional.empty();
    }
}
