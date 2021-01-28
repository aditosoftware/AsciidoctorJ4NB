package org.netbeans.asciidoc;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.jruby.internal.JRubyAsciidoctor;

import java.util.List;

public final class AsciidoctorConverter
{

  private static final LazyValue<AsciidoctorConverter> DEFAULT_REF = new LazyValue<>(AsciidoctorConverter::new);

  private final Asciidoctor doctor;

  private AsciidoctorConverter()
  {
    doctor = JRubyAsciidoctor.create(List.of(
        // Base
        "uri:classloader:/META-INF/jruby.home/lib/ruby/2.0",

        // Asciidoctor
        "uri:classloader:/gems/asciidoctor-2.0.12/lib",
        "uri:classloader:/gems/coderay-1.1.3/lib",
        "uri:classloader:/gems/erubis-2.7.0/lib",
        "uri:classloader:/gems/haml-5.0.4/lib",
        "uri:classloader:/gems/open-uri-cached-0.0.5/lib",
        "uri:classloader:/gems/slim-4.0.1/lib",
        "uri:classloader:/gems/temple-0.8.2/lib",
        "uri:classloader:/gems/tilt-2.0.9/lib",

        // Asciidoctor-PDF
        "uri:classloader:/gems/asciidoctor-pdf-1.5.4/lib",
        "uri:classloader:/gems/addressable-2.4.0/lib",
        "uri:classloader:/gems/concurrent-ruby-1.1.7/lib",
        "uri:classloader:/gems/afm-0.2.2/lib",
        "uri:classloader:/gems/Ascii85-1.0.3/lib",
        "uri:classloader:/gems/css_parser-1.7.1/lib",
        "uri:classloader:/gems/hashery-2.1.2/lib",
        "uri:classloader:/gems/pdf-core-0.7.0/lib",
        "uri:classloader:/gems/pdf-reader-2.4.1/lib",
        "uri:classloader:/gems/polyglot-0.3.5/lib",
        "uri:classloader:/gems/prawn-2.2.2/lib",
        "uri:classloader:/gems/prawn-icon-2.5.0/lib",
        "uri:classloader:/gems/prawn-svg-0.31.0/lib",
        "uri:classloader:/gems/prawn-table-0.2.2/lib",
        "uri:classloader:/gems/prawn-templates-0.1.2/lib",
        "uri:classloader:/gems/public_suffix-1.4.6/lib",
        "uri:classloader:/gems/rghost-0.9.7/lib",
        "uri:classloader:/gems/rouge-3.26.0/lib",
        "uri:classloader:/gems/ruby-rc4-0.1.5/lib",
        "uri:classloader:/gems/safe_yaml-1.0.5/lib",
        "uri:classloader:/gems/text-hyphen-1.4.1/lib",
        "uri:classloader:/gems/thread_safe-0.3.6-java/lib",
        "uri:classloader:/gems/treetop-1.6.11/lib",
        "uri:classloader:/gems/ttfunk-1.5.1/lib"
    ));
  }

  public static AsciidoctorConverter getDefault()
  {
    return DEFAULT_REF.get();
  }

  public Asciidoctor getDoctor()
  {
    return doctor;
  }

}
