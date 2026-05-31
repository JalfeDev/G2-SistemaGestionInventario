# 🚀 Configuración de React + Vite

Guía para instalar React usando Vite dentro de un repositorio existente.

---

## 📋 Requisitos

Antes de empezar asegúrate de tener instalado:

- Node.js >= 18
- npm

Verifica las versiones:

```bash
node -v
npm -v
```

---

## 📦 Crear el proyecto con Vite

Desde la raíz del repositorio ejecuta:

```bash
npm create vite@latest
```

Selecciona:

```txt
✔ Project name: frontend
✔ Framework: React
✔ Variant: JavaScript / TypeScript
```

Esto creará una carpeta llamada `frontend`.

---

## 📂 Entrar al proyecto

```bash
cd frontend
```

---

## 📥 Instalar dependencias

```bash
npm install
```

---

## ▶️ Ejecutar el proyecto

```bash
npm run dev
```

Vite mostrará una URL similar a:

```txt
http://localhost:5173
```

---

## 🏗️ Estructura básica

```txt
frontend/
├── public/
├── src/
│   ├── assets/
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
├── package.json
├── vite.config.js
└── index.html
```

---

## 📦 Scripts disponibles

```bash
npm run dev      # Desarrollo
npm run build    # Build de producción
npm run preview  # Vista previa del build
```

---

## 🔧 Instalar dependencias comunes

### React Router

```bash
npm install react-router-dom
```

### Axios

```bash
npm install axios
```

### Tailwind CSS

```bash
npm install tailwindcss @tailwindcss/vite
```

---

## 🌿 Recomendaciones Git

Agregar `node_modules` al `.gitignore`:

```gitignore
node_modules
dist
.env
```

---
## ✅ Build de producción
```bash
npm run build
```
Los archivos finales se generarán en:
```txt
dist/
```
---

## 📚 Recursos

- https://vitejs.dev/
- https://react.dev/
