import { useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { searchBuyers } from '../api/buyers'
import type { BuyerResponse } from '../types/order'

const DEBOUNCE_MS = 300
const MIN_LENGTH = 2

export function useBuyerSearch(firstName: string, lastName: string) {
  const [suggestions, setSuggestions] = useState<BuyerResponse[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [hasSearched, setHasSearched] = useState(false)

  useEffect(() => {
    const first = firstName.trim()
    const last = lastName.trim()
    const canSearch = first.length >= MIN_LENGTH || last.length >= MIN_LENGTH

    if (!canSearch) {
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
        const results = await searchBuyers(firstName, lastName)
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
  }, [firstName, lastName])

  return { suggestions, loading, error, hasSearched }
}
