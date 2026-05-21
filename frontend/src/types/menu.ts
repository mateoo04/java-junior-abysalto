export interface MenuItemResponse {
  menuItemId: number
  name: string
  price: number
  active: boolean
}

export interface CreateMenuItemRequest {
  name: string
  price: number
}

export interface CreateOrderLineRequest {
  menuItemId: number
  quantity: number
}

export type CartLine = {
  menuItemId: number
  name: string
  price: number
  quantity: number
}
