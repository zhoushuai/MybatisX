package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.MapperClassGenerateFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生成mybatis的xml文件内容.
 * 第一版在这里预留一个形式
 * 后续可以加入:  springjpa提示, 生成mybatis注解
 */
public class MybatisXmlGenerator implements Generator {

    /**
     * The constant ID.
     */
    public static final String ID = "id";
    /**
     * The constant RESULT_MAP.
     */
    public static final String RESULT_MAP = "resultMap";
    private MapperClassGenerateFactory mapperClassGenerateFactory;
    private Mapper mapper;
    private Project project;

    /**
     * The constant RESULT_TYPE.
     */
    public static final String RESULT_TYPE = "resultType";

    /**
     * Instantiates a new Mybatis xml generator.
     *
     * @param mapperClassGenerateFactory
     * @param mapper                     the mapper
     * @param project                    the project
     */
    public MybatisXmlGenerator(MapperClassGenerateFactory mapperClassGenerateFactory, Mapper mapper, @NotNull Project project) {
        this.mapperClassGenerateFactory = mapperClassGenerateFactory;
        this.mapper = mapper;
        this.project = project;
    }

    @Override
    public void generateSelect(String id, String value, String resultMap, String resultType) {
        XmlTag xmlTag = mapper.ensureTagExists();
        XmlTag select = xmlTag.createChildTag("select", null, value, false);
        select.setAttribute(ID, id);
        // 是否被映射结果集
        boolean resultMapped = false;
        if (StringUtils.isNotBlank(resultMap)) {
            select.setAttribute(RESULT_MAP, resultMap);
            resultMapped = true;
        }
        if (!resultMapped && StringUtils.isNotBlank(resultType)) {
            select.setAttribute(RESULT_TYPE, resultType);
        }

        xmlTag.addSubTag(select, false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(select);


        if (logger.isDebugEnabled()) {
            logger.debug("the text of select tag is :"+select.getText());
        }
        mapperClassGenerateFactory.generateMethod();

    }

    private static final Logger logger = LoggerFactory.getLogger(MybatisXmlGenerator.class);

    @Override
    public void generateDelete(String id, String value) {
        XmlTag delete = mapper.ensureTagExists().createChildTag("delete", null, value, false);
        delete.setAttribute(ID, id);
        mapper.ensureTagExists().addSubTag(delete, false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(delete);

        mapperClassGenerateFactory.generateMethod();
    }

    @Override
    public void generateInsert(String id, String value) {
        XmlTag insert = mapper.ensureTagExists().createChildTag("insert", null, value, false);
        insert.setAttribute(ID, id);
        XmlTag xmlTag = mapper.ensureTagExists();
        xmlTag.addSubTag(insert, false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(insert);

        mapperClassGenerateFactory.generateMethod();

    }

    @Override
    public void generateUpdate(String id, String value) {
        XmlTag update = mapper.ensureTagExists().createChildTag("update", null, value, false);
        update.setAttribute(ID, id);
        mapper.ensureTagExists().addSubTag(update, false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(update);

        mapperClassGenerateFactory.generateMethod();

    }
}
