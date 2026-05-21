import { useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { searchMenuItems } from '../api/menuItems'
import type { MenuItemResponse } from '../types/menu'

const DEBOUNCE_MS = 300
const MIN_LENGTH = 2

export function useMenuSearch(name: string) {
  const [suggestions, setSuggestions] = useState<MenuItemResponse[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [hasSearched, setHasSearched] = useState(false)

  useEffect(() => {
    const query = name.trim()
    if (query.length < MIN_LENGTH) {
      setSuggestions([])
      setLoading(false)
      setError(null)
      setHasSearched(false)
      return
    }

    const controller = new AbortController()
    const timeout = window.setTimeout(async () => {
      setLoading(true)
      setError(null)
      try {
        const results = await searchMenuItems(query)
        if (!controller.signal.aborted) {
          setSuggestions(results)
          setHasSearched(true)
        }
      } catch (err) {
        if (!controller.signal.aborted) {
          setSuggestions([])
          setHasSearched(true)
          setError(err instanceof ApiError ? err.message : 'Search failed')
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false)
        }
      }
    }, DEBOUNCE_MS)

    return () => {
      controller.abort()
      window.clearTimeout(timeout)
    }
  }, [name])

  return { suggestions, loading, error, hasSearched }
}
