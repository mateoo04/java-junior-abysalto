export type OrderStatus =
  | 'WAITING_FOR_CONFIRMATION'
  | 'PREPARING'
  | 'DONE'

export type PaymentOption = 'CASH' | 'CARD_UPFRONT' | 'CARD_ON_DELIVERY'

export interface AddressResponse {
  buyerAddressId: number
  city: string
  street: string
  homeNumber: string | null
}

export interface OrderItemResponse {
  orderItemId: number
  itemNr: number
  name: string
  quantity: number
  price: number
}

export interface OrderResponse {
  orderNr: number
  buyerId: number
  buyerName: string
  orderStatus: OrderStatus
  orderTime: string
  paymentOption: PaymentOption
  deliveryAddress: AddressResponse
  contactNumber: string
  note: string | null
  items: OrderItemResponse[]
  currency: string
  totalPrice: number
}

export interface CreateBuyerRequest {
  firstName: string
  lastName: string
  title?: string
}

export interface CreateAddressRequest {
  city: string
  street: string
  homeNumber?: string
}

export interface CreateOrderItemRequest {
  name: string
  quantity: number
  price: number
}

export interface CreateOrderRequest {
  buyer: CreateBuyerRequest
  paymentOption: PaymentOption
  deliveryAddress: CreateAddressRequest
  contactNumber: string
  note?: string
  currency: string
  items: CreateOrderItemRequest[]
}

export interface UpdateOrderStatusRequest {
  orderStatus: OrderStatus
}
