<template>
  <div class="user">
    <div class="user-image" @click="router.push('/personalHomepage')">
      <el-avatar :src="avatarUrl"></el-avatar>
    </div>
    <div class="user-info">
      <div>{{ nickname }}</div>
      <div>{{ handleTime(createTime) }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { handleTime } from '@/utils/common.ts'
import { useRouter } from 'vue-router'
const router = useRouter()
interface Props {
  avatarUrl: string
  nickname: string
  createTime: string
}
defineProps<Props>()
</script>

<style lang="less" scoped>
.user {
  display: flex;
  align-items: center;
  .user-image {
    margin-right: 0.5em;

    &:deep(.el-avatar) {
      vertical-align: bottom;
    }
    &:hover {
      cursor: pointer;
    }
  }
  .user-info {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
}
</style>
