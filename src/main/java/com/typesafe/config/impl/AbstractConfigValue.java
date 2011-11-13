package com.typesafe.config.impl;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValue;

abstract class AbstractConfigValue implements ConfigValue {

    final private ConfigOrigin origin;

    AbstractConfigValue(ConfigOrigin origin) {
        this.origin = origin;
    }

    @Override
    public ConfigOrigin origin() {
        return this.origin;
    }

    /**
     * Called only by SubstitutionResolver object.
     *
     * @param resolver
     *            the resolver doing the resolving
     * @param depth
     *            the number of substitutions followed in resolving the current
     *            one
     * @param withFallbacks
     *            whether to look at system props and env vars
     * @return a new value if there were changes, or this if no changes
     */
    AbstractConfigValue resolveSubstitutions(SubstitutionResolver resolver,
            int depth,
            boolean withFallbacks) {
        return this;
    }

    ResolveStatus resolveStatus() {
        return ResolveStatus.RESOLVED;
    }

    /**
     * This is used when including one file in another; the included file is
     * relativized to the path it's included into in the parent file. The point
     * is that if you include a file at foo.bar in the parent, and the included
     * file as a substitution ${a.b.c}, the included substitution now needs to
     * be ${foo.bar.a.b.c} because we resolve substitutions globally only after
     * parsing everything.
     *
     * @param prefix
     * @return value relativized to the given path or the same value if nothing
     *         to do
     */
    AbstractConfigValue relativized(Path prefix) {
        return this;
    }

    protected interface Modifier {
        AbstractConfigValue modifyChild(AbstractConfigValue v);
    }

    @Override
    public AbstractConfigValue withFallback(ConfigValue other) {
        return this;
    }

    AbstractConfigValue transformed(ConfigTransformer transformer) {
        return this;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfigValue;
    }

    @Override
    public boolean equals(Object other) {
        // note that "origin" is deliberately NOT part of equality
        if (other instanceof ConfigValue) {
            return canEqual(other)
                    && (this.valueType() ==
                            ((ConfigValue) other).valueType())
                    && ConfigUtil.equalsHandlingNull(this.unwrapped(),
                            ((ConfigValue) other).unwrapped());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // note that "origin" is deliberately NOT part of equality
        Object o = this.unwrapped();
        if (o == null)
            return 0;
        else
            return o.hashCode();
    }

    @Override
    public String toString() {
        return valueType().name() + "(" + unwrapped() + ")";
    }

    // toString() is a debugging-oriented string but this is defined
    // to create a string that would parse back to the value in JSON.
    // It only works for primitive values (that would be a single token)
    // which are auto-converted to strings when concatenating with
    // other strings or by the DefaultTransformer.
    String transformToString() {
        return null;
    }
}
