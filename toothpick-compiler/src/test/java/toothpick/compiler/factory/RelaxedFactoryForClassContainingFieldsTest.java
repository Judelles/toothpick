package toothpick.compiler.factory;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class RelaxedFactoryForClassContainingFieldsTest extends BaseFactoryTest {
  @Test
  public void testRelaxedFactoryCreationForInjectedField() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.TestRelaxedFactoryCreationForInjectField", Joiner.on('\n').join(//
        "package test;", //
        "import javax.inject.Inject;", //
        "import toothpick.ScopeInstances;", //
        "public class TestRelaxedFactoryCreationForInjectField {", //
        "  @Inject Foo foo;", //
        "}", //
        "  class Foo {}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestRelaxedFactoryCreationForInjectField$$Factory", Joiner.on('\n').join(//
        "package test;", //
        "import java.lang.Override;", //
        "import toothpick.Factory;", //
        "import toothpick.MemberInjector;", //
        "import toothpick.Scope;", //
        "", //
        "public final class TestRelaxedFactoryCreationForInjectField$$Factory implements Factory<TestRelaxedFactoryCreationForInjectField> {", //
        "  private MemberInjector<TestRelaxedFactoryCreationForInjectField> memberInjector = "
            + "new test.TestRelaxedFactoryCreationForInjectField$$MemberInjector();",
        //
        "  @Override", //
        "  public TestRelaxedFactoryCreationForInjectField createInstance(Scope scope) {", //
        "    scope = getTargetScope(scope);", //
        "    TestRelaxedFactoryCreationForInjectField testRelaxedFactoryCreationForInjectField = new TestRelaxedFactoryCreationForInjectField();", //
        "    memberInjector.inject(testRelaxedFactoryCreationForInjectField, scope);", //
        "    return testRelaxedFactoryCreationForInjectField;", //
        "  }", //
        "  @Override", //
        "  public Scope getTargetScope(Scope scope) {", //
        "    return scope;", //
        "  }", //
        "  @Override", //
        "  public boolean hasScopeAnnotation() {", //
        "    return false;", //
        "  }", //
        "  @Override", //
        "  public boolean hasScopeInstancesAnnotation() {", //
        "    return false;", //
        "  }", //
        "}" //
    ));

    assert_().about(javaSource())
        .that(source)
        .processedWith(ProcessorTestUtilities.factoryAndMemberInjectorProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test
  public void testRelaxedFactoryCreationForInjectedField_shouldFail_WhenFieldIsInvalid() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.TestRelaxedFactoryCreationForInjectField", Joiner.on('\n').join(//
        "package test;", //
        "import javax.inject.Inject;", //
        "import toothpick.ScopeInstances;", //
        "public class TestRelaxedFactoryCreationForInjectField {", //
        "  @Inject private Foo foo;", //
        "}", //
        "  class Foo {}"));

    assert_().about(javaSource())
        .that(source)
        .processedWith(ProcessorTestUtilities.factoryAndMemberInjectorProcessors())
        .failsToCompile()
        .withErrorContaining("@Inject annotated fields must be non private : test.TestRelaxedFactoryCreationForInjectField#foo");
  }

  @Test
  public void testRelaxedFactoryCreationForInjectedField_shouldWorkButNoFactoryIsProduced_whenTypeIsAbstract() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.TestRelaxedFactoryCreationForInjectField", Joiner.on('\n').join(//
        "package test;", //
        "import javax.inject.Inject;", //
        "import toothpick.ScopeInstances;", //
        "public abstract class TestRelaxedFactoryCreationForInjectField {", //
        "  @Inject Foo foo;", //
        "}", //
        "  class Foo {}"));

    assertThatCompileWithoutErrorButNoFactoryIsCreated(source, "test", "TestRelaxedFactoryCreationForInjectField");
  }

  @Test
  public void testRelaxedFactoryCreationForInjectedField_shouldWorkButNoFactoryIsProduced_whenTypeHasANonDefaultConstructor() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.TestRelaxedFactoryCreationForInjectField", Joiner.on('\n').join(//
        "package test;", //
        "import javax.inject.Inject;", //
        "import toothpick.ScopeInstances;", //
        "public class TestRelaxedFactoryCreationForInjectField {", //
        "  @Inject Foo foo;", //
        "  public TestRelaxedFactoryCreationForInjectField(String s) {}", //
        "}", //
        "class Foo {}"));

    assertThatCompileWithoutErrorButNoFactoryIsCreated(source, "test", "TestRelaxedFactoryCreationForInjectField");
  }

  @Test
  public void testRelaxedFactoryCreationForInjectedField_shouldWorkButNoFactoryIsProduced_whenTypeHasAPrivateDefaultConstructor() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.TestRelaxedFactoryCreationForInjectField", Joiner.on('\n').join(//
        "package test;", //
        "import javax.inject.Inject;", //
        "import toothpick.ScopeInstances;", //
        "public class TestRelaxedFactoryCreationForInjectField {", //
        "  @Inject Foo foo;", //
        "  private TestRelaxedFactoryCreationForInjectField() {}", //
        "}", //
        "class Foo {}"));

    assertThatCompileWithoutErrorButNoFactoryIsCreated(source, "test", "TestRelaxedFactoryCreationForInjectField");
  }
}
