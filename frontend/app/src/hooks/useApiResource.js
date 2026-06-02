import { useCallback, useEffect, useRef, useState } from 'react'
import { getApiError, isCanceledRequest } from '../services/api'

const EMPTY_DATA = []

export function useApiResource(load, initialData = EMPTY_DATA) {
  const loader = useRef(load)
  const [data, setData] = useState(initialData)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchData = useCallback(async () => {
    try {
      const response = await loader.current()
      setData(response.data)
      setError('')
    } catch (requestError) {
      if (isCanceledRequest(requestError)) return
      setData(initialData)
      setError(getApiError(requestError))
    } finally {
      setLoading(false)
    }
  }, [initialData])

  const reload = useCallback(() => {
    setLoading(true)
    fetchData()
  }, [fetchData])

  useEffect(() => {
    fetchData()
  }, [fetchData])

  return { data, setData, loading, error, reload }
}
