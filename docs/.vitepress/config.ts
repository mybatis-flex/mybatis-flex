import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  lang: 'zh-CN',
  title: "Mybatis-Flex",
  description: "一个优雅的 Mybatis 增强框架",
  // logo: '/assets/images/logo02.png',

  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    // logo: '/assets/images/logo01.png',
    nav: [
      { text: '首页', link: '/' },
      { text: '帮助文档', link: '/zh/what-is-mybatisflex' },
      { text: '更新日志', link: 'https://gitee.com/mybatis-flex/mybatis-flex/releases' },
      { text: '示例代码', link: 'https://gitee.com/mybatis-flex/mybatis-flex-samples' },
      { text: '源码',  items: [
          { text: 'Gitee', link: 'https://gitee.com/mybatis-flex/mybatis-flex' },
          { text: 'Github', link: 'https://github.com/mybatis-flex/mybatis-flex' }
        ] },
    ],

    sidebar: [
      {
        text: '简介',
        items: [
          { text: 'Mybatis-Flex 是什么', link: '/zh/what-is-mybatisflex' },
          { text: '快速开始', link: '/zh/getting-started' },
          { text: 'Maven 依赖', link: '/zh/maven' },
          { text: '和 Spring 整合', link: '/zh/mybatis-flex-with-spring' },
          { text: '和同类框架对比', link: '/zh/comparison' },
          { text: 'QQ 交流群', link: '/zh/qq_group' },
        ]
      },
      {
        text: '基础功能',
        items: [
          { text: '增删改', link: '/zh/add-delete-update' },
          { text: '查询和分页', link: '/zh/query' },
          { text: 'QueryWrapper', link: '/zh/querywrapper' },
        ]
      },
      {
        text: '核心功能',
        items: [
          { text: '@Table 注解', link: '/zh/table' },
          { text: '@Id 注解', link: '/zh/id' },
          { text: '@Column 注解', link: '/zh/column' },
          { text: 'Db + Row', link: '/zh/db_row' },
          { text: '逻辑删除', link: '/zh/logic_delete' },
          { text: '乐观锁', link: '/zh/version' },
          { text: '数据填充', link: '/zh/fill' },
          { text: '数据脱敏', link: '/zh/mask' },
          { text: 'SQL 审计', link: '/zh/audit' },
          { text: '多数据源', link: '/zh/multi-datasource' },
          { text: '事务管理', link: '/zh/tx' },
          { text: '字段权限', link: '/zh/columns_permission' },
          { text: '字段加密', link: '/zh/columns_encrypt' },
          { text: '字典回显', link: '/zh/columns_dict' },
        ]
      },
      {
        text: '其他',
        items: [
          { text: '代码生成器', link: '/zh/codegen' },
          { text: 'APT 设置', link: '/zh/apt' },
        ]
      }
    ],

    footer: {
      message: 'Released under the Apache License.',
      copyright: 'Copyright © 2022-present Mybatis-Flex，<span style="font-size: 12px">备案号：<a style="color:#777" target="_blank" rel="noopener" href="http://beian.miit.gov.cn/">黔ICP备19009310号-9 </a></span>'
    }
  },
  head: [
    [
      'link',{ rel: 'icon', href: '/assets/images/logo02.png' }
    ],

    [
      // 添加百度统计
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
    ]
  ],
})
