<!--.vitepress/theme/MyLayout.vue-->
<script setup>
import DefaultTheme from 'vitepress/theme'

const {Layout} = DefaultTheme
</script>

<template>
  <Layout>
    <!--docs: https://vitepress.dev/guide/extending-default-theme#layout-slots-->
    <template #doc-footer-before>
      <div class="info custom-block" style="margin-bottom: 14px">
        <!--        <p class="custom-block-title">对这篇文章还有疑问？</p>-->
        <p>对这篇文章还有疑问？ 点击 <a href="https://gitee.com/mybatis-flex/mybatis-flex/issues/new" target="_blank">这里</a> 发起提问。
        </p>
      </div>
    </template>
  </Layout>
</template>