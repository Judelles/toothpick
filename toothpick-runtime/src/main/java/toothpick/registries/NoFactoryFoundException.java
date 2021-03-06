package toothpick.registries;

import static java.lang.String.format;

public class NoFactoryFoundException extends RuntimeException {
  public NoFactoryFoundException(Class clazz) {
    this(clazz, null);
  }

  public NoFactoryFoundException(Class clazz, Throwable cause) {
    super(format("No factory could be found for class %s." //
        + " Check that registries are properly setup with annotation processor arguments, " //
        + "or use annotations correctly in this class.", clazz.getName()), cause);
  }

}
