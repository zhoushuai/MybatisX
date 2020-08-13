package com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping;


import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 在接口上加入注释, 注释内部加入注解:  @Entity com.xx.xx.User
 */
public class CommentAnnotationMappingResolver extends JpaMappingResolver implements EntityMappingResolver {


    public static final String TABLE_ENTITY = "Entity";

    @Override
    public List<TxField> getFields() {
        return fieldList;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public Optional<PsiClass> findEntity(PsiClass mapperClass) {
        PsiDocComment docComment = mapperClass.getDocComment();
        if (docComment != null) {
            PsiDocTag tableEntity = docComment.findTagByName(TABLE_ENTITY);
            if (tableEntity != null) {
                String entityText = tableEntity.getValueElement().getText();
                if (StringUtils.isNotBlank(entityText)) {
                    JavaPsiFacade instance = JavaPsiFacade.getInstance(mapperClass.getProject());
                    PsiClass entityClass = instance.findClass(entityText, mapperClass.getResolveScope());
                    if (entityClass != null) {
                        initDataByCamel(entityClass);
                        return Optional.of(entityClass);
                    }
                }
            }
        }

        return Optional.empty();
    }

    /**
     * 字段列表
     */
    private List<TxField> fieldList;
    /**
     * 表名
     */
    private String tableName;

    private void initDataByCamel(PsiClass entityClass) {
        fieldList = Arrays.stream(entityClass.getAllFields()).map(field -> {
            TxField txField = new TxField();
            txField.setTipName(com.baomidou.plugin.idea.mybatisx.smartjpa.util.StringUtils.upperCaseFirstChar(field.getName()));
            txField.setFieldType(field.getType().getCanonicalText());

            String columnName = getColumnNameByJpaOrCamel(field);
            // 实体的字段名称
            txField.setFieldName(field.getName());
            // 表的列名
            txField.setColumnName(columnName);

            return txField;
        }).collect(Collectors.toList());

        tableName = getTableNameByJpaOrCamel(entityClass);
    }


}