package playground.solrmarc.index.extractor.methodcall;

import java.lang.reflect.Method;
import java.util.Collection;

public class MultiValueMappingMethodCall extends AbstractMappingMethodCall<Collection<String>>
{
    private final Object mixin;
    private final Method method;

    public MultiValueMappingMethodCall(final Object mixin, final Method method)
    {
        super(mixin.getClass().getSimpleName(), method.getName());
        this.mixin = mixin;
        this.method = method;

        if (!Collection.class.isAssignableFrom(this.method.getReturnType()))
        {
            throw new IllegalArgumentException(
                    "The method's return type has to be assignable to Collection:\nMixin class:  "
                            + mixin.getClass().getName() + "\nMixin method: " + method.toString());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<String> invoke(final Object[] parameters) throws Exception
    {
        return (Collection<String>) method.invoke(mixin, parameters);
    }
}