package ir.stts.bajet.db.search.constant;

public enum SearchOperationEnm {

    and,
    or,

    equals,
    notEqual,

    greaterThan,
    greaterOrEqual,

    lessThan,
    lessOrEqual,

    contains,
    startsWith,
    endsWith,
    notContains,
    notStartsWith,
    notEndsWith,

    between,
    betweenInclusive,

    isBlank,
    notBlank,

    inSet,
    notInSet,

    isNull,
    notNull
}