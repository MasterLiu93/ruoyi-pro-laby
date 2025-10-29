<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="900px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="SKU编码" prop="skuCode">
            <el-input v-model="formData.skuCode" placeholder="请输入SKU编码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="商品名称" prop="goodsName">
            <el-input v-model="formData.goodsName" placeholder="请输入商品名称" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品分类" prop="categoryId">
            <el-tree-select
              v-model="formData.categoryId"
              :data="categoryTree"
              :props="{ label: 'categoryName', value: 'id' }"
              placeholder="请选择商品分类"
              clearable
              filterable
              check-strictly
              class="!w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="品牌" prop="brand">
            <el-input v-model="formData.brand" placeholder="请输入品牌" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="型号" prop="model">
            <el-input v-model="formData.model" placeholder="请输入型号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="条形码" prop="barcode">
            <el-input v-model="formData.barcode" placeholder="请输入条形码" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="计量单位" prop="unit">
            <el-select v-model="formData.unit" placeholder="请选择计量单位" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_GOODS_UNIT)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="规格" prop="spec">
            <el-input v-model="formData.spec" placeholder="请输入规格" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="重量(KG)" prop="weight">
            <el-input-number v-model="formData.weight" :min="0" :precision="3" placeholder="请输入重量" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="体积(m³)" prop="volume">
            <el-input-number v-model="formData.volume" :min="0" :precision="3" placeholder="请输入体积" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="保质期(天)" prop="shelfLife">
            <el-input-number v-model="formData.shelfLife" :min="0" placeholder="0表示无保质期" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="存储温度" prop="storageTempMin">
            <el-col :span="11">
              <el-input-number v-model="formData.storageTempMin" :precision="1" placeholder="最低温度" class="!w-full" />
            </el-col>
            <el-col :span="2" class="text-center">
              <span class="text-gray-500">~</span>
            </el-col>
            <el-col :span="11">
              <el-input-number v-model="formData.storageTempMax" :precision="1" placeholder="最高温度" class="!w-full" />
            </el-col>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="安全库存" prop="safetyStock">
            <el-input-number v-model="formData.safetyStock" :min="0" :precision="2" placeholder="低于此值告警" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最大库存" prop="maxStock">
            <el-input-number v-model="formData.maxStock" :min="0" :precision="2" placeholder="高于此值告警" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="批次管理" prop="needBatch">
            <el-switch v-model="formData.needBatch" active-text="启用" inactive-text="禁用" />
            <span class="ml-2 text-gray-400 text-xs">食品、药品等需批次追踪</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="序列号管理" prop="needSerial">
            <el-switch v-model="formData.needSerial" active-text="启用" inactive-text="禁用" />
            <span class="ml-2 text-gray-400 text-xs">手机、电脑等贵重物品</span>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                :key="dict.value"
                :label="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { handleTree } from '@/utils/tree'
import * as GoodsApi from '@/api/wms/goods'
import * as GoodsCategoryApi from '@/api/wms/category'

/** 商品信息 表单 */
defineOptions({ name: 'GoodsForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  skuCode: undefined,
  goodsName: undefined,
  categoryId: undefined,
  brand: undefined,
  model: undefined,
  barcode: undefined,
  unit: 1,
  spec: undefined,
  weight: undefined,
  volume: undefined,
  shelfLife: undefined,
  storageTempMin: undefined,
  storageTempMax: undefined,
  needBatch: false,
  needSerial: false,
  safetyStock: 0,
  maxStock: undefined,
  status: 1,
  remark: undefined
})
const formRules = reactive({
  skuCode: [{ required: true, message: 'SKU编码不能为空', trigger: 'blur' }],
  goodsName: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择计量单位', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref
const categoryTree = ref([]) // 分类树

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await GoodsApi.getGoods(id)
    } finally {
      formLoading.value = false
    }
  }
  // 加载分类树
  const categoryList = await GoodsCategoryApi.getGoodsCategorySimpleList()
  categoryTree.value = handleTree(categoryList, 'id', 'parentId')
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as GoodsApi.GoodsVO
    if (formType.value === 'create') {
      await GoodsApi.createGoods(data)
      message.success(t('common.createSuccess'))
    } else {
      await GoodsApi.updateGoods(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    skuCode: undefined,
    goodsName: undefined,
    categoryId: undefined,
    brand: undefined,
    model: undefined,
    barcode: undefined,
    unit: 1,
    spec: undefined,
    weight: undefined,
    volume: undefined,
    shelfLife: undefined,
    storageTempMin: undefined,
    storageTempMax: undefined,
    needBatch: false,
    needSerial: false,
    safetyStock: 0,
    maxStock: undefined,
    status: 1,
    remark: undefined
  }
  formRef.value?.resetFields()
}
</script>

