package com.arrety.domainrepository.domainpersistence.common;

/**
 * @author arrety
 * @date 2022/2/7 19:27
 */
public enum SqlKeyword {
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    ASC("ASC"),
    DESC("DESC"),
    LIMIT("limit");

    private final String keyword;

    public String getSqlSegment() {
        return this.keyword;
    }

    private SqlKeyword(final String keyword) {
        this.keyword = keyword;
    }
}