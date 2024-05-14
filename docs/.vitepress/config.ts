import {defineConfig} from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
    lang: 'zh-CN',
    title: "MyBatis-Flex",
    titleTemplate: ':title - MyBatis-Flex 官方网站',
    description: "一个优雅的 MyBatis 增强框架",
    lastUpdated: true,

    // logo: '/assets/images/logo02.png',

    themeConfig: {
        outline: {
            label: "章节"
        },
        search: {
            provider: 'local'
        },
        editLink: {
            // pattern: 'https://github.com/mybatis-flex/mybatis-flex/edit/main/docs/:path'
            pattern: 'https://gitee.com/mybatis-flex/mybatis-flex/edit/main/docs/:path',
            text: '编辑当前页面'
        },
        // https://vitepress.dev/reference/default-theme-config
        // logo: '/assets/images/logo01.png',
        nav: [
            {text: '首页', link: '/'},

            {text: '帮助文档', link: '/zh/intro/what-is-mybatisflex'},
            {text: '🆘 常见问题', link: '/zh/faq'},
            {text: '周边 / 视频教程', link: '/zh/awesome-things'},
            {text: '🍷代码生成器（AI）', link: '/zh/others/codegen.md'},
            {text: '更新日志', link: '/zh/changes'},
            {text: 'JavaDoc', link: 'https://apidoc.gitee.com/mybatis-flex/mybatis-flex/'},

            {
                text: '获取源码', items: [
                    {text: 'Gitee', link: 'https://gitee.com/mybatis-flex/mybatis-flex'},
                    {text: 'Github', link: 'https://github.com/mybatis-flex/mybatis-flex'},
                    {text: '示例代码', link: 'https://gitee.com/mybatis-flex/mybatis-flex-samples'},
                    {text: '性能测试代码', link: 'https://gitee.com/mybatis-flex/mybatis-benchmark'},
                    {text: 'MyBatis-Flex-Admin', link: 'https://gitee.com/mybatis-flex/mybatis-flex-admin'},
                    {text: 'MyBatis-Flex-Kotlin', link: 'https://gitee.com/mybatis-flex/mybatis-flex-kotlin'},
                ]
            },
        ],

        sidebar: [
            {
                text: '简介',
                items: [
                    {text: 'MyBatis-Flex 是什么', link: '/zh/intro/what-is-mybatisflex'},
                    {text: '快速开始', link: '/zh/intro/getting-started'},
                    {text: 'Maven 依赖', link: '/zh/intro/maven'},
                    {text: 'Gradle 依赖', link: '/zh/intro/gradle'},
                    {text: 'Kotlin 使用', link: '/zh/intro/use-in-kotlin'},
                    {text: '和同类框架「功能」对比', link: '/zh/intro/comparison'},
                    {text: '和同类框架「性能」对比', link: '/zh/intro/benchmark'},
                    {text: '使用 Mybatis 原生功能', link: '/zh/intro/use-mybatis-native'},
                    {text: '支持的数据库类型', link: '/zh/intro/support-database'},
                    {text: '微信（或QQ）交流群', link: '/zh/intro/qq-group'},
                ]
            },
            {
                text: '基础功能',
                items: [
                    {text: '增、删、改', link: '/zh/base/add-delete-update'},
                    {text: '基础查询', link: '/zh/base/query'},
                    {text: '自动映射', link: '/zh/base/auto-mapping'},
                    {text: '关联查询', link: '/zh/base/relations-query'},
                    {text: '批量操作', link: '/zh/base/batch'},
                    {text: '链式操作🔥🔥', link: '/zh/base/chain'},
                    {text: 'QueryWrapper', link: '/zh/base/querywrapper'},
                    {text: 'Db + Row', link: '/zh/base/db-row'},
                    {text: 'Active Record', link: '/zh/base/active-record'},
                    {text: 'IService', link: '/zh/base/service'},
                    {text: 'SpringBoot 配置文件', link: '/zh/base/configuration'},
                    {text: 'MyBatisFlexCustomizer', link: '/zh/base/mybatis-flex-customizer'},
                ]
            },
            {
                text: '核心功能',
                items: [
                    {text: '@Table 注解', link: '/zh/core/table'},
                    {text: '@Id 注解', link: '/zh/core/id'},
                    {text: '@Column 注解', link: '/zh/core/column'},
                    {text: '逻辑删除', link: '/zh/core/logic-delete'},
                    {text: '乐观锁', link: '/zh/core/version'},
                    {text: '数据填充', link: '/zh/core/fill'},
                    {text: '数据脱敏', link: '/zh/core/mask'},
                    {text: '数据缓存', link: '/zh/core/data-cache'},
                    {text: 'SQL 审计', link: '/zh/core/audit'},
                    {text: 'SQL 打印', link: '/zh/core/sql-print'},
                    {text: '多数据源', link: '/zh/core/multi-datasource'},
                    {text: '读写分离 💥', link: '/zh/core/read-write-splitting'},
                    {text: '数据源加密', link: '/zh/core/datasource-encryption'},
                    {text: '动态表名', link: '/zh/core/dynamic-table'},
                    {text: '事务管理', link: '/zh/core/tx'},
                    {text: '数据权限', link: '/zh/core/data-permission'},
                    {text: '字段权限', link: '/zh/core/columns-permission'},
                    {text: '字段加密', link: '/zh/core/columns-encrypt'},
                    {text: '字典回写', link: '/zh/core/columns-dict'},
                    {text: '枚举属性', link: '/zh/core/enum-property'},
                    {text: '多租户', link: '/zh/core/multi-tenancy'},
                ]
            },
            {
                text: '其他',
                items: [
                    {text: '代码生成器', link: '/zh/others/codegen'},
                    {text: 'APT 设置', link: '/zh/others/apt'},
                    {text: 'KAPT 设置', link: '/zh/others/kapt'},
                ]
            }
        ],

        footer: {
            message: 'Released under the Apache License.',
            copyright: 'Copyright © 2022-present Mybatis-Flex. ' +
                '<span style="display: flex;align-items: center;justify-content: center;">' +
                '<span style="font-size: 12px;margin-right:10px;"><a style="color:#777" target="_blank" rel="noopener" href="http://beian.miit.gov.cn/">黔ICP备19009310号-9 </a></span>' +

                '<img src="/assets/images/beian.jpg" style="margin-top: -2px;margin-right: 2px;width: 15px;">' +

                '<a target="_blank" href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=52010202003658"' +
                ' style="display:inline-block;text-decoration:none;color:#777;font-size: 12px">贵公网安备 52010202003658 号</a>' +
                '</span>'
        }
    },
    head: [
        [
            'link', {rel: 'icon', href: '/assets/images/logo02.png'}
        ],

        [// 添加百度统计
            "script",
            {},
            `
      var _hmt = _hmt || [];
      (function() {
        var hm = document.createElement("script");
        hm.src = "https://hm.baidu.com/hm.js?3f50d5fbe3bf955411748b5616b24a24";
        var s = document.getElementsByTagName("script")[0];
        s.parentNode.insertBefore(hm, s);
      })();
        `
        ],

        [// 自动跳转 https
            "script",
            {},
            `
        if (location.protocol !== 'https:' && location.hostname != 'localhost') {
            location.href = 'https://' + location.hostname + location.pathname + location.search;
        }
        `
        ]
    ],
})
