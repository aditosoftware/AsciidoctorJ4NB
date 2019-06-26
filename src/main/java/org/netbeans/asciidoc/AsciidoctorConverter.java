package org.netbeans.asciidoc;

import org.asciidoctor.*;
import org.jruby.*;

import java.util.Arrays;

public final class AsciidoctorConverter
{
  private static final LazyValue<AsciidoctorConverter> DEFAULT_REF = new LazyValue<>(AsciidoctorConverter::new);

  private final Asciidoctor doctor;

  private AsciidoctorConverter()
  {
    //Ruby.getGlobalRuntime().getInstanceConfig().setCompileMode(RubyInstanceConfig.CompileMode.JIT);
    this.doctor = Asciidoctor.Factory.create(Arrays.asList("gems/asciidoctor-1.5.4/lib",
                                                           "gems/coderay-1.1.0/lib",
                                                           "META-INF/jruby.home/lib/ruby/2.0",

                                                           // Asciidoctor-PDF
                                                           "META-INF/jruby.home/lib/ruby/1.9",
                                                           //"META-INF/jruby.home/lib/ruby/shared", //todo statt "shared" komplett aufzunehmen sollten evtl nur die nötigen Dependencies eingebunden werden -> https://github.com/jruby/warbler/issues/266
                                                           "gems/thread_safe-0.3.5-java/lib",
                                                           "gems/asciidoctor-pdf-1.5.0.alpha.11/lib",
                                                           "gems/addressable-2.4.0/lib",
                                                           "gems/afm-0.2.2/lib",
                                                           "gems/Ascii85-1.0.2/lib",
                                                           "gems/css_parser-1.3.7/lib",
                                                           "gems/hashery-2.1.1/lib",
                                                           "gems/pdf-core-0.4.0/lib",
                                                           "gems/pdf-reader-1.3.3/lib",
                                                           "gems/polyglot-0.3.5/lib",
                                                           "gems/prawn-1.3.0/lib",
                                                           "gems/prawn-icon-1.0.0/lib",
                                                           "gems/prawn-svg-0.21.0/lib",
                                                           "gems/prawn-table-0.2.2/lib",
                                                           "gems/prawn-templates-0.0.3/lib",
                                                           "gems/rouge-1.10.1/lib",
                                                           "gems/ruby-rc4-0.1.5/lib",
                                                           "gems/safe_yaml-1.0.4/lib",
                                                           "gems/treetop-1.5.3/lib",
                                                           "gems/ttfunk-1.4.0/lib"
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

  public String convert(String src, Options options)
  {
    return doctor.convert(src, options);
  }
}
