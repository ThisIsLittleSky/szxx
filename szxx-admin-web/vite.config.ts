import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5174,
    proxy: {
      '/api/v1': {
        // 关键修改：改成后端实际端口 8089
        target: 'http://localhost:8089', 
        changeOrigin: true,
        rewrite: (path) => path
      }
    }
  }
})