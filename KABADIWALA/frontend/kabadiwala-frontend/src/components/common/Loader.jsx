import React from 'react'

export default function Loader({ text = 'Loading...' }) {
  return (
    <div className="loading-overlay">
      <div className="spinner"></div>
      <span>{text}</span>
    </div>
  )
}
