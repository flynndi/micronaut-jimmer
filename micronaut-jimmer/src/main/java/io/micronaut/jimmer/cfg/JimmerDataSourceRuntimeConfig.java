package io.micronaut.jimmer.cfg;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.naming.Named;
import java.util.Collection;
import org.babyfish.jimmer.sql.EnumType;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.dialect.Dialect;
import org.babyfish.jimmer.sql.event.TriggerType;
import org.babyfish.jimmer.sql.fetcher.ReferenceFetchType;
import org.babyfish.jimmer.sql.runtime.DatabaseValidationMode;
import org.babyfish.jimmer.sql.runtime.IdOnlyTargetCheckingLevel;

@EachProperty("micronaut.jimmer.datasources")
public class JimmerDataSourceRuntimeConfig implements Named {

    private final String name;

    private Dialect dialect;

    private boolean showSql;

    private boolean prettySql;

    private boolean inlineSqlVariables;

    private ReferenceFetchType defaultReferenceFetchType = ReferenceFetchType.SELECT;

    private int maxJoinFetchDepth = 3;

    private DatabaseValidationMode databaseValidationMode = DatabaseValidationMode.NONE;

    private DatabaseValidation databaseValidation = new DatabaseValidation();

    private TriggerType triggerType = TriggerType.BINLOG_ONLY;

    private boolean defaultDissociationActionCheckable;

    private IdOnlyTargetCheckingLevel idOnlyTargetCheckingLevel = IdOnlyTargetCheckingLevel.NONE;

    private EnumType.Strategy defaultEnumStrategy = EnumType.Strategy.NAME;

    private String defaultSchema = "";

    private int defaultBatchSize = JSqlClient.Builder.DEFAULT_BATCH_SIZE;

    private int defaultListBatchSize = JSqlClient.Builder.DEFAULT_LIST_BATCH_SIZE;

    private boolean inListPaddingEnabled;

    private boolean expandedInListPaddingEnabled;

    private boolean dissociationLogicalDeleteEnabled;

    private int offsetOptimizingThreshold = Integer.MAX_VALUE;

    private boolean reverseSortOptimizationEnabled;

    private boolean isForeignKeyEnabledByDefault = true;

    private int maxCommandJoinCount = 2;

    private boolean mutationTransactionRequired;

    private boolean targetTransferable;

    private boolean explicitBatchEnabled;

    private boolean dumbBatchAcceptable;

    private boolean constraintViolationTranslatable = true;

    private Collection<String> executorContextPrefixes;

    public JimmerDataSourceRuntimeConfig(@Parameter String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public boolean isShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public boolean isPrettySql() {
        return prettySql;
    }

    public void setPrettySql(boolean prettySql) {
        this.prettySql = prettySql;
    }

    public boolean isInlineSqlVariables() {
        return inlineSqlVariables;
    }

    public void setInlineSqlVariables(boolean inlineSqlVariables) {
        this.inlineSqlVariables = inlineSqlVariables;
    }

    public ReferenceFetchType getDefaultReferenceFetchType() {
        return defaultReferenceFetchType;
    }

    public void setDefaultReferenceFetchType(ReferenceFetchType defaultReferenceFetchType) {
        this.defaultReferenceFetchType = defaultReferenceFetchType;
    }

    public int getMaxJoinFetchDepth() {
        return maxJoinFetchDepth;
    }

    public void setMaxJoinFetchDepth(int maxJoinFetchDepth) {
        this.maxJoinFetchDepth = maxJoinFetchDepth;
    }

    public DatabaseValidationMode getDatabaseValidationMode() {
        return databaseValidationMode;
    }

    public void setDatabaseValidationMode(DatabaseValidationMode databaseValidationMode) {
        this.databaseValidationMode = databaseValidationMode;
    }

    public DatabaseValidation getDatabaseValidation() {
        return databaseValidation;
    }

    public void setDatabaseValidation(DatabaseValidation databaseValidation) {
        this.databaseValidation = databaseValidation;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public boolean isDefaultDissociationActionCheckable() {
        return defaultDissociationActionCheckable;
    }

    public void setDefaultDissociationActionCheckable(boolean defaultDissociationActionCheckable) {
        this.defaultDissociationActionCheckable = defaultDissociationActionCheckable;
    }

    public IdOnlyTargetCheckingLevel getIdOnlyTargetCheckingLevel() {
        return idOnlyTargetCheckingLevel;
    }

    public void setIdOnlyTargetCheckingLevel(IdOnlyTargetCheckingLevel idOnlyTargetCheckingLevel) {
        this.idOnlyTargetCheckingLevel = idOnlyTargetCheckingLevel;
    }

    public EnumType.Strategy getDefaultEnumStrategy() {
        return defaultEnumStrategy;
    }

    public void setDefaultEnumStrategy(EnumType.Strategy defaultEnumStrategy) {
        this.defaultEnumStrategy = defaultEnumStrategy;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public int getDefaultBatchSize() {
        return defaultBatchSize;
    }

    public void setDefaultBatchSize(int defaultBatchSize) {
        this.defaultBatchSize = defaultBatchSize;
    }

    public int getDefaultListBatchSize() {
        return defaultListBatchSize;
    }

    public void setDefaultListBatchSize(int defaultListBatchSize) {
        this.defaultListBatchSize = defaultListBatchSize;
    }

    public boolean isInListPaddingEnabled() {
        return inListPaddingEnabled;
    }

    public void setInListPaddingEnabled(boolean inListPaddingEnabled) {
        this.inListPaddingEnabled = inListPaddingEnabled;
    }

    public boolean isExpandedInListPaddingEnabled() {
        return expandedInListPaddingEnabled;
    }

    public void setExpandedInListPaddingEnabled(boolean expandedInListPaddingEnabled) {
        this.expandedInListPaddingEnabled = expandedInListPaddingEnabled;
    }

    public boolean isDissociationLogicalDeleteEnabled() {
        return dissociationLogicalDeleteEnabled;
    }

    public void setDissociationLogicalDeleteEnabled(boolean dissociationLogicalDeleteEnabled) {
        this.dissociationLogicalDeleteEnabled = dissociationLogicalDeleteEnabled;
    }

    public int getOffsetOptimizingThreshold() {
        return offsetOptimizingThreshold;
    }

    public void setOffsetOptimizingThreshold(int offsetOptimizingThreshold) {
        this.offsetOptimizingThreshold = offsetOptimizingThreshold;
    }

    public boolean isReverseSortOptimizationEnabled() {
        return reverseSortOptimizationEnabled;
    }

    public void setReverseSortOptimizationEnabled(boolean reverseSortOptimizationEnabled) {
        this.reverseSortOptimizationEnabled = reverseSortOptimizationEnabled;
    }

    public boolean isForeignKeyEnabledByDefault() {
        return isForeignKeyEnabledByDefault;
    }

    public void setForeignKeyEnabledByDefault(boolean foreignKeyEnabledByDefault) {
        isForeignKeyEnabledByDefault = foreignKeyEnabledByDefault;
    }

    public int getMaxCommandJoinCount() {
        return maxCommandJoinCount;
    }

    public void setMaxCommandJoinCount(int maxCommandJoinCount) {
        this.maxCommandJoinCount = maxCommandJoinCount;
    }

    public boolean isMutationTransactionRequired() {
        return mutationTransactionRequired;
    }

    public void setMutationTransactionRequired(boolean mutationTransactionRequired) {
        this.mutationTransactionRequired = mutationTransactionRequired;
    }

    public boolean isTargetTransferable() {
        return targetTransferable;
    }

    public void setTargetTransferable(boolean targetTransferable) {
        this.targetTransferable = targetTransferable;
    }

    public boolean isExplicitBatchEnabled() {
        return explicitBatchEnabled;
    }

    public void setExplicitBatchEnabled(boolean explicitBatchEnabled) {
        this.explicitBatchEnabled = explicitBatchEnabled;
    }

    public boolean isDumbBatchAcceptable() {
        return dumbBatchAcceptable;
    }

    public void setDumbBatchAcceptable(boolean dumbBatchAcceptable) {
        this.dumbBatchAcceptable = dumbBatchAcceptable;
    }

    public boolean isConstraintViolationTranslatable() {
        return constraintViolationTranslatable;
    }

    public void setConstraintViolationTranslatable(boolean constraintViolationTranslatable) {
        this.constraintViolationTranslatable = constraintViolationTranslatable;
    }

    public Collection<String> getExecutorContextPrefixes() {
        return executorContextPrefixes;
    }

    public void setExecutorContextPrefixes(Collection<String> executorContextPrefixes) {
        this.executorContextPrefixes = executorContextPrefixes;
    }

    public static class DatabaseValidation {

        private DatabaseValidationMode mode = DatabaseValidationMode.NONE;

        public DatabaseValidationMode getMode() {
            return mode;
        }

        public void setMode(DatabaseValidationMode mode) {
            this.mode = mode;
        }
    }
}
