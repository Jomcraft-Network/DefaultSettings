/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {

    String modid();

    String name() default "";

    String version() default "";

    String dependencies() default "";

    boolean useMetadata() default false;

    String acceptedMinecraftVersions() default "";

    String acceptableRemoteVersions() default "";

    String acceptableSaveVersions() default "";

    String bukkitPlugin() default "";

    String certificateFingerprint() default "";

    String modLanguage() default "java";

    String modLanguageAdapter() default "";

    @Deprecated
    String asmHookClass() default "";

    boolean canBeDeactivated() default false;

    String guiFactory() default "";

    CustomProperty[] customProperties() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target( {})
    public @interface CustomProperty {
        String k();

        String v();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface EventHandler {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Instance {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Metadata {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface InstanceFactory {
    }
}