<!--
  客户管理列表页
  
  功能说明：
  1. 客户的查询、新增、编辑、删除
  2. 支持多条件搜索（客户编码、名称、类型、等级、联系人、电话、状态）
  3. 支持分页显示
  4. 显示累计订单数和累计金额
  5. 权限控制：wms:customer:query、create、update、delete
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item label="客户编码" prop="customerCode">
        <el-input
          v-model="queryParams.customerCode"
          placeholder="请输入客户编码"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="客户名称" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户名称"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="客户类型" prop="customerType">
        <el-select
          v-model="queryParams.customerType"
          placeholder="请选择客户类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_CUSTOMER_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="客户等级" prop="customerLevel">
        <el-select
          v-model="queryParams.customerLevel"
          placeholder="请选择客户等级"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_CUSTOMER_LEVEL)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input
          v-model="queryParams.contactPerson"
          placeholder="请输入联系人"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input
          v-model="queryParams.contactPhone"
          placeholder="请输入联系电话"
          clearable
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
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetimerange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" />
          重置
        </el-button>
        <el-button type="primary" @click="openForm('create')" v-hasPermi="['wms:customer:create']">
          <Icon icon="ep:plus" class="mr-5px" />
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="客户编码" prop="customerCode" min-width="120" />
      <el-table-column label="客户名称" prop="customerName" min-width="180" show-overflow-tooltip />
      <el-table-column label="客户类型" prop="customerType" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_CUSTOMER_TYPE" :value="scope.row.customerType" />
        </template>
      </el-table-column>
      <el-table-column label="客户等级" prop="customerLevel" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_CUSTOMER_LEVEL" :value="scope.row.customerLevel" />
        </template>
      </el-table-column>
      <el-table-column label="联系人" prop="contactPerson" width="100" show-overflow-tooltip />
      <el-table-column label="联系电话" prop="contactPhone" width="120" />
      <el-table-column label="收货地址" min-width="200" show-overflow-tooltip>
        <template #default="scope">
          {{ [scope.row.deliveryProvince, scope.row.deliveryCity, scope.row.deliveryDistrict, scope.row.deliveryAddress].filter(Boolean).join(' ') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="信用额度" prop="creditLimit" width="110" align="right">
        <template #default="scope">
          <span style="color: #409eff; font-weight: 500">{{ formatToFraction(scope.row.creditLimit) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="累计订单" prop="totalOrders" width="90" align="right" />
      <el-table-column label="累计金额" prop="totalAmount" width="110" align="right">
        <template #default="scope">
          <span style="color: #67c23a; font-weight: 500">{{ formatToFraction(scope.row.totalAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" :formatter="dateFormatter" />
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['wms:customer:update']">
            编辑
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['wms:customer:delete']">
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
  <CustomerForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { formatToFraction } from '@/utils'
import * as CustomerApi from '@/api/wms/customer'
import CustomerForm from './CustomerForm.vue'

/**
 * 客户管理列表页组件定义
 */
defineOptions({ name: 'WmsCustomer' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true) // 列表加载中
const list = ref([]) // 列表数据
const total = ref(0) // 总条数

// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  customerCode: undefined,
  customerName: undefined,
  customerType: undefined,
  customerLevel: undefined,
  contactPerson: undefined,
  contactPhone: undefined,
  status: undefined,
  createTime: undefined
})
const queryFormRef = ref()

/**
 * 查询列表
 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CustomerApi.getCustomerPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/**
 * 搜索按钮操作
 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/**
 * 重置按钮操作
 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/**
 * 打开表单
 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/**
 * 删除操作
 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await CustomerApi.deleteCustomer(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/**
 * 初始化
 */
onMounted(() => {
  getList()
})
</script>

