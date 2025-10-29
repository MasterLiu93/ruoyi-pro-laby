<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="SKU编码" prop="skuCode">
        <el-input
          v-model="queryParams.skuCode"
          placeholder="请输入SKU编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="商品分类" prop="categoryId">
        <el-tree-select
          v-model="queryParams.categoryId"
          :data="categoryTree"
          :props="{ label: 'categoryName', value: 'id' }"
          placeholder="请选择商品分类"
          clearable
          filterable
          check-strictly
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="品牌" prop="brand">
        <el-input
          v-model="queryParams.brand"
          placeholder="请输入品牌"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery" type="primary">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['wms:goods:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增商品
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="SKU编码" align="center" prop="skuCode" width="150px" />
      <el-table-column label="商品名称" align="center" prop="goodsName" min-width="200px" show-overflow-tooltip />
      
      <el-table-column label="分类" align="center" width="130px">
        <template #default="scope">
          <el-tag v-if="scope.row.categoryName" type="info" size="small">
            {{ scope.row.categoryName }}
          </el-tag>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      
      <el-table-column label="品牌" align="center" prop="brand" width="120px">
        <template #default="scope">
          {{ scope.row.brand || '-' }}
        </template>
      </el-table-column>
      
      <el-table-column label="型号" align="center" prop="model" width="120px" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.model || '-' }}
        </template>
      </el-table-column>
      
      <el-table-column label="单位" align="center" width="90px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_GOODS_UNIT" :value="scope.row.unit" />
        </template>
      </el-table-column>
      
      <el-table-column label="规格" align="center" prop="spec" width="150px" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.spec || '-' }}
        </template>
      </el-table-column>
      
      <el-table-column label="安全库存" align="center" prop="safetyStock" width="100px">
        <template #default="scope">
          <el-tag v-if="scope.row.safetyStock > 0" type="warning" size="small">
            {{ scope.row.safetyStock }}
          </el-tag>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      
      <el-table-column label="批次/序列号" align="center" width="120px">
        <template #default="scope">
          <div class="flex gap-1 justify-center">
            <el-tag v-if="scope.row.needBatch" type="warning" size="small">批次</el-tag>
            <el-tag v-if="scope.row.needSerial" type="danger" size="small">序列</el-tag>
            <span v-if="!scope.row.needBatch && !scope.row.needSerial" class="text-gray-400">-</span>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column label="状态" align="center" width="80px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      
      <el-table-column label="创建时间" align="center" prop="createTime" width="180px">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      
      <el-table-column label="操作" align="center" fixed="right" width="180px">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['wms:goods:update']">
            编辑
          </el-button>
          <el-button link type="primary" @click="openDetail(scope.row)" v-hasPermi="['wms:goods:query']">
            详情
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['wms:goods:delete']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗 -->
  <GoodsForm ref="formRef" @success="getList" />

  <!-- 详情弹窗 -->
  <Dialog title="商品详情" v-model="detailVisible" width="900px">
    <el-descriptions :column="2" border v-loading="detailLoading">
      <el-descriptions-item label="SKU编码">
        <el-tag type="primary">{{ detailData.skuCode }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="商品名称">
        <strong>{{ detailData.goodsName }}</strong>
      </el-descriptions-item>
      <el-descriptions-item label="商品分类">
        {{ detailData.categoryName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="品牌">
        {{ detailData.brand || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="型号">
        {{ detailData.model || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="条形码">
        {{ detailData.barcode || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="计量单位">
        <dict-tag :type="DICT_TYPE.WMS_GOODS_UNIT" :value="detailData.unit" />
      </el-descriptions-item>
      <el-descriptions-item label="规格">
        {{ detailData.spec || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="重量(KG)">
        {{ detailData.weight || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="体积(m³)">
        {{ detailData.volume || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="保质期(天)">
        {{ detailData.shelfLife || '无保质期' }}
      </el-descriptions-item>
      <el-descriptions-item label="存储温度(℃)">
        <span v-if="detailData.storageTempMin || detailData.storageTempMax">
          {{ detailData.storageTempMin || '-' }} ~ {{ detailData.storageTempMax || '-' }}
        </span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="批次管理">
        <el-tag :type="detailData.needBatch ? 'success' : 'info'">
          {{ detailData.needBatch ? '启用' : '禁用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="序列号管理">
        <el-tag :type="detailData.needSerial ? 'success' : 'info'">
          {{ detailData.needSerial ? '启用' : '禁用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="安全库存">
        <el-tag type="warning">{{ detailData.safetyStock || 0 }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="最大库存">
        {{ detailData.maxStock || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="detailData.status" />
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(detailData.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">
        {{ detailData.remark || '-' }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { handleTree } from '@/utils/tree'
import * as GoodsApi from '@/api/wms/goods'
import * as GoodsCategoryApi from '@/api/wms/category'
import GoodsForm from './GoodsForm.vue'

/** 商品信息 列表 */
defineOptions({ name: 'WmsGoods' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  skuCode: undefined,
  goodsName: undefined,
  categoryId: undefined,
  brand: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const categoryTree = ref([]) // 分类树

// 详情相关
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref({} as GoodsApi.GoodsVO)

/** 格式化日期 */
const formatDate = (date: any) => {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await GoodsApi.getGoodsPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 打开详情 */
const openDetail = async (row: GoodsApi.GoodsVO) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await GoodsApi.getGoods(row.id!)
  } finally {
    detailLoading.value = false
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await GoodsApi.deleteGoods(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 加载分类树
  const categoryList = await GoodsCategoryApi.getGoodsCategorySimpleList()
  categoryTree.value = handleTree(categoryList, 'id', 'parentId')
})
</script>

