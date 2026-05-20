import { request } from './client'
import type { BuyerResponse } from '../types/order'

export function searchBuyers(
  firstName: string,
  lastName: string,
): Promise<BuyerResponse[]> {
  const params = new URLSearchParams()
  const first = firstName.trim()
  const last = lastName.trim()
  if (first) params.set('firstName', first)
  if (last) params.set('lastName', last)
  const query = params.toString()
  return request<BuyerResponse[]>(`/buyers/search${query ? `?${query}` : ''}`)
}
