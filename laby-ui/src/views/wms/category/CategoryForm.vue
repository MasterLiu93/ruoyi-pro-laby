<!--
  商品分类表单组件
  
  功能说明：
  1. 支持新增和编辑商品分类
  2. 表单字段：分类编码、分类名称、父分类ID、层级、排序
  3. 必填项验证
  4. 成功后自动刷新列表
  
  使用方法：
  <CategoryForm ref="formRef" @success="getList" />
  formRef.value.open('create') // 新增
  formRef.value.open('update', id) // 编辑
  
  @author laby
  @date 2025-10-28
-->
<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <!-- 分类编码 -->
      <el-form-item label="分类编码" prop="categoryCode">
        <el-input v-model="formData.categoryCode" placeholder="请输入分类编码，如：CAT-001" />
      </el-form-item>
      <!-- 分类名称 -->
      <el-form-item label="分类名称" prop="categoryName">
        <el-input v-model="formData.categoryName" placeholder="请输入分类名称，如：电子产品" />
      </el-form-item>
      <!-- 父分类ID -->
      <el-form-item label="父分类ID" prop="parentId">
        <el-input-number v-model="formData.parentId" :min="0" placeholder="0表示顶级分类" class="!w-full" />
      </el-form-item>
      <!-- 层级 -->
      <el-form-item label="层级" prop="level">
        <el-input-number v-model="formData.level" :min="1" :max="3" placeholder="1-一级，2-二级，3-三级" class="!w-full" />
      </el-form-item>
      <!-- 排序 -->
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="formData.sort" :min="0" placeholder="数字越小越靠前" class="!w-full" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import * as GoodsCategoryApi from '@/api/wms/category'

/** 商品分类 表单 */
defineOptions({ name: 'CategoryForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改

// 表单数据
const formData = ref({
  id: undefined, // 分类ID（编辑时必传）
  categoryCode: undefined, // 分类编码
  categoryName: undefined, // 分类名称
  parentId: 0, // 父分类ID，默认0表示顶级分类
  level: 1, // 层级，默认1级
  sort: 0 // 排序，默认0
})

// 表单验证规则
const formRules = reactive({
  categoryCode: [{ required: true, message: '分类编码不能为空', trigger: 'blur' }],
  categoryName: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }],
  level: [{ required: true, message: '请选择层级', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/**
 * 打开弹窗
 * @param type 操作类型：create-新增，update-修改
 * @param id 分类ID（修改时必传）
 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await GoodsCategoryApi.getGoodsCategory(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/**
 * 提交表单
 * 根据formType执行新增或更新操作
 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as GoodsCategoryApi.GoodsCategoryVO
    if (formType.value === 'create') {
      // 新增
      await GoodsCategoryApi.createGoodsCategory(data)
      message.success(t('common.createSuccess'))
    } else {
      // 更新
      await GoodsCategoryApi.updateGoodsCategory(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件，通知父组件刷新列表
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/**
 * 重置表单
 * 将表单数据恢复到初始状态
 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    categoryCode: undefined,
    categoryName: undefined,
    parentId: 0,
    level: 1,
    sort: 0
  }
  formRef.value?.resetFields()
}
</script>
