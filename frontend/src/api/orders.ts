import { request } from './client'
import type {
  CreateOrderRequest,
  OrderResponse,
  OrderStatus,
  UpdateOrderStatusRequest,
} from '../types/order'

export type OrderSort = 'default' | 'totalPrice' | '-totalPrice'

function sortParam(sort: OrderSort): string | undefined {
  if (sort === 'totalPrice') return 'totalPrice'
  if (sort === '-totalPrice') return '-totalPrice'
  return undefined
}

export function listOrders(sort: OrderSort = 'default'): Promise<OrderResponse[]> {
  const param = sortParam(sort)
  const query = param ? `?sort=${encodeURIComponent(param)}` : ''
  return request<OrderResponse[]>(`/api/orders${query}`)
}

export function getOrder(orderNr: number): Promise<OrderResponse> {
  return request<OrderResponse>(`/api/orders/${orderNr}`)
}

export function createOrder(body: CreateOrderRequest): Promise<OrderResponse> {
  return request<OrderResponse>('/api/orders', { method: 'POST', body })
}

export function updateOrderStatus(
  orderNr: number,
  orderStatus: OrderStatus,
): Promise<OrderResponse> {
  const body: UpdateOrderStatusRequest = { orderStatus }
  return request<OrderResponse>(`/api/orders/${orderNr}/status`, { method: 'PATCH', body })
}
