<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="入库单号" prop="inboundNo">
        <el-input
          v-model="queryParams.inboundNo"
          placeholder="请输入入库单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="入库类型" prop="inboundType">
        <el-select
          v-model="queryParams.inboundType"
          placeholder="请选择入库类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_INBOUND_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="仓库" prop="warehouseId">
        <el-select
          v-model="queryParams.warehouseId"
          placeholder="请选择仓库"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="warehouse in warehouseList"
            :key="warehouse.id"
            :label="warehouse.warehouseName"
            :value="warehouse.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_INBOUND_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['wms:inbound:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="入库单号" align="center" prop="inboundNo" min-width="170" />
      <el-table-column label="类型" align="center" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_INBOUND_TYPE" :value="scope.row.inboundType" />
        </template>
      </el-table-column>
      <el-table-column label="仓库" align="center" prop="warehouseName" width="130" show-overflow-tooltip />
      <el-table-column label="供应商" align="center" prop="supplierName" width="150" show-overflow-tooltip>
        <template #default="scope">
          <span class="text-gray-600">{{ scope.row.supplierName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总数量" align="center" prop="totalQuantity" width="90" />
      <el-table-column label="已收货" align="center" prop="receivedQuantity" width="90" />
      <el-table-column label="总金额" align="center" prop="totalAmount" width="120">
        <template #default="scope">
          <span v-if="scope.row.totalAmount" class="text-red-500 font-semibold">
            ¥{{ scope.row.totalAmount.toFixed(2) }}
          </span>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_INBOUND_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="165" />
      <el-table-column label="操作" align="center" fixed="right" min-width="230">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row)" v-hasPermi="['wms:inbound:query']">
            详情
          </el-button>
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['wms:inbound:update']" v-if="scope.row.status === 1">
            编辑
          </el-button>
          <el-button link type="success" @click="handleAudit(scope.row)" v-hasPermi="['wms:inbound:audit']" v-if="scope.row.status === 1">
            审核
          </el-button>
          <el-button link type="primary" @click="handleReceive(scope.row)" v-hasPermi="['wms:inbound:receive']" v-if="scope.row.status === 2 || scope.row.status === 3">
            收货
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['wms:inbound:delete']" v-if="scope.row.status === 1">
            删除
          </el-button>
          <el-button link type="warning" @click="handleCancel(scope.row.id)" v-hasPermi="['wms:inbound:cancel']" v-if="scope.row.status !== 4 && scope.row.status !== 5">
            取消
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

  <!-- 表单弹窗：添加/修改 -->
  <InboundForm ref="formRef" @success="getList" />

  <!-- 详情弹窗 -->
  <InboundDetail ref="detailRef" />

  <!-- 收货弹窗 -->
  <InboundReceive ref="receiveRef" @success="getList" />
</template>

<script setup lang="ts" name="WmsInbound">
import { dateFormatter } from '@/utils/formatTime'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { getInboundPage, deleteInbound, auditInbound, cancelInbound } from '@/api/wms/inbound'
import { getWarehouseSimpleList } from '@/api/wms/warehouse'
import InboundForm from './InboundForm.vue'
import InboundDetail from './InboundDetail.vue'
import InboundReceive from './InboundReceive.vue'

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  inboundNo: undefined,
  inboundType: undefined,
  warehouseId: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const warehouseList = ref([]) // 仓库列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await getInboundPage(queryParams)
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

/** 详情操作 */
const detailRef = ref()
const openDetail = (row: any) => {
  detailRef.value.open(row.id)
}

/** 收货操作 */
const receiveRef = ref()
const handleReceive = (row: any) => {
  receiveRef.value.open(row.id)
}

/** 审核操作 */
const handleAudit = async (row: any) => {
  try {
    await message.confirm('确认审核通过该入库单吗？')
    // TODO: 获取当前用户信息
    await auditInbound(row.id, 1, 'admin')
    message.success('审核成功')
    await getList()
  } catch {}
}

/** 取消操作 */
const handleCancel = async (id: number) => {
  try {
    await message.confirm('确认取消该入库单吗？')
    await cancelInbound(id)
    message.success('取消成功')
    await getList()
  } catch {}
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await deleteInbound(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 初始化仓库列表 */
const initWarehouseList = async () => {
  const data = await getWarehouseSimpleList()
  warehouseList.value = data
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  await initWarehouseList()
})
</script>

