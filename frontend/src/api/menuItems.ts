import { request } from './client'
import type { CreateMenuItemRequest, MenuItemResponse } from '../types/menu'

export function listMenuItems(): Promise<MenuItemResponse[]> {
  return request<MenuItemResponse[]>('/api/menu-items')
}

export function searchMenuItems(name: string): Promise<MenuItemResponse[]> {
  const trimmed = name.trim()
  if (!trimmed) {
    return Promise.resolve([])
  }
  const query = new URLSearchParams({ name: trimmed })
  return request<MenuItemResponse[]>(`/api/menu-items/search?${query}`)
}

export function createMenuItem(body: CreateMenuItemRequest): Promise<MenuItemResponse> {
  return request<MenuItemResponse>('/api/menu-items', { method: 'POST', body })
}
