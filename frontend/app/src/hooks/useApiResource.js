import { useCallback, useEffect, useRef, useState } from 'react'
import { getApiError } from '../services/api'

export function useApiResource(load, fallbackData) {
  const loader = useRef(load)
  const [data, setData] = useState(fallbackData)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchData = useCallback(async () => {
    try {
      const response = await loader.current()
      setData(response.data)
      setError('')
    } catch (requestError) {
      setData(fallbackData)
      setError(getApiError(requestError))
    } finally {
      setLoading(false)
    }
  }, [fallbackData])

  const reload = useCallback(() => {
    setLoading(true)
    fetchData()
  }, [fetchData])

  useEffect(() => {
    fetchData()
  }, [fetchData])

  return { data, setData, loading, error, reload }
}
