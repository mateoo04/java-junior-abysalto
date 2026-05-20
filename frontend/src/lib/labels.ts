import type { OrderStatus, PaymentOption } from '../types/order'

const orderStatusLabels: Record<OrderStatus, string> = {
  WAITING_FOR_CONFIRMATION: 'Waiting',
  PREPARING: 'Preparing',
  DONE: 'Done',
}

const paymentOptionLabels: Record<PaymentOption, string> = {
  CASH: 'Cash',
  CARD_UPFRONT: 'Card upfront',
  CARD_ON_DELIVERY: 'Card on delivery',
}

export function orderStatusLabel(status: OrderStatus): string {
  return orderStatusLabels[status]
}

export function paymentOptionLabel(option: PaymentOption): string {
  return paymentOptionLabels[option]
}

export const ORDER_STATUSES = Object.keys(orderStatusLabels) as OrderStatus[]
export const PAYMENT_OPTIONS = Object.keys(paymentOptionLabels) as PaymentOption[]
