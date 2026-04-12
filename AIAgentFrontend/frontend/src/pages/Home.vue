<template>
  <section class="home">
    <div class="home-hero">
      <span class="pill">极简新拟态</span>
      <h1>缘鱼的 AI 应用工作台</h1>
      <p>在这里切换不同的 AI 应用，感受轻盈的对话体验。</p>
    </div>
    <div class="home-grid">
      <router-link to="/assistant" class="card">
        <h2>AI 助手</h2>
        <p>随时开启一段温柔的对话。</p>
      </router-link>
      <router-link to="/agent" class="card">
        <h2>AI 智能体</h2>
        <p>更具行动力的智能交互体验。</p>
      </router-link>
    </div>
  </section>
</template>
