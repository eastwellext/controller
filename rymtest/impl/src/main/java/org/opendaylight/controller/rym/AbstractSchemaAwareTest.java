package org.opendaylight.controller.rym;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import org.opendaylight.mdsal.binding.generator.impl.ModuleInfoBackedContext;
import org.opendaylight.mdsal.binding.spec.reflect.BindingReflections;
import org.opendaylight.yangtools.yang.binding.YangModuleInfo;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;

import java.util.Set;

public abstract class AbstractSchemaAwareTest {
    private static final LoadingCache<ClassLoader, Set<YangModuleInfo>> MODULE_INFO_CACHE = CacheBuilder.newBuilder()
            .weakKeys().weakValues().build(new CacheLoader<ClassLoader, Set<YangModuleInfo>>() {
                @Override
                public Set<YangModuleInfo> load(final ClassLoader key) {
                    return BindingReflections.loadModuleInfos(key);
                }
            });
    private static final LoadingCache<Set<YangModuleInfo>, SchemaContext> SCHEMA_CONTEXT_CACHE =
            CacheBuilder.newBuilder().weakValues().build(new CacheLoader<Set<YangModuleInfo>, SchemaContext>() {
                @Override
                public SchemaContext load(final Set<YangModuleInfo> key) {
                    final ModuleInfoBackedContext moduleContext = ModuleInfoBackedContext.create();
                    moduleContext.addModuleInfos(key);
                    return moduleContext.tryToCreateSchemaContext().get();
                }
            });

    protected Set<YangModuleInfo> getModuleInfos() throws Exception {
        return MODULE_INFO_CACHE.getUnchecked(Thread.currentThread().getContextClassLoader());
    }

    protected SchemaContext getSchemaContext() throws Exception {
        // ImmutableSet guarantees non-null
        return SCHEMA_CONTEXT_CACHE.getUnchecked(ImmutableSet.copyOf(getModuleInfos()));
    }

    public final void setup() throws Exception {
        setupWithSchema(getSchemaContext());
    }

    /**
     * Setups test with Schema context.
     * This method is called before {@link #setupWithSchemaService(SchemaService)}
     */
    protected abstract void setupWithSchema(SchemaContext context);

}
