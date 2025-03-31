"use client"

import { useState, useEffect } from "react"
import { Card, CardContent } from "@/components/ui/card"

interface Restaurant {
    id: number
    borough: string
    name: string
    category: string
}

export default function Home() {
    const [restaurants, setRestaurants] = useState<Restaurant[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        fetch("/api/restaurants")
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Response was not ok")
                }
                return response.json()
            })
            .then((data) => {
                console.log(`Loaded ${data.length} restaurants`)
                setRestaurants(data)
                setLoading(false)
            })
            .catch((error) => {
                console.error("Error fetching restaurants:", error)
                setError("Error loading restaurants.")
                setLoading(false)
            })
    }, [])

    if (loading) {
        return (
            <div className="min-h-screen flex flex-col">
                <header className="bg-[#ff6b6b] text-white py-8 text-center">
                    <h1 className="text-4xl font-bold mb-2">GoodEats</h1>
                    <p className="text-xl opacity-90">Discover the Best Restaurants in London!</p>
                </header>
                <main className="flex-grow flex items-center justify-center">
                    <div className="text-center text-gray-600 text-lg">Loading restaurants...</div>
                </main>
                <footer className="bg-gray-800 text-white text-center py-6">
                    <p>&copy; 2025 GoodEats</p>
                </footer>
            </div>
        )
    }

    if (error) {
        return (
            <div className="min-h-screen flex flex-col">
                <header className="bg-[#ff6b6b] text-white py-8 text-center">
                    <h1 className="text-4xl font-bold mb-2">GoodEats</h1>
                    <p className="text-xl opacity-90">Discover the Best Restaurants in London!</p>
                </header>
                <main className="flex-grow flex items-center justify-center">
                    <div className="text-center text-red-600 text-lg">{error}</div>
                </main>
                <footer className="bg-gray-800 text-white text-center py-6">
                    <p>&copy; 2025 GoodEats</p>
                </footer>
            </div>
        )
    }

    return (
        <div className="min-h-screen flex flex-col">
            <header className="bg-[#ff6b6b] text-white py-8 text-center">
                <h1 className="text-4xl font-bold mb-2">GoodEats</h1>
                <p className="text-xl opacity-90">Discover the Best Restaurants in London!</p>
            </header>

            <main className="flex-grow">
                <div className="container mx-auto px-4 py-8">
                    <div className="text-center mb-8">
                        <h2 className="text-2xl font-bold">All {restaurants.length} London Restaurants</h2>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                        {restaurants.length === 0 ? (
                            <div className="col-span-full text-center text-gray-600 text-lg">No restaurants found.</div>
                        ) : (
                            restaurants.map((restaurant) => (
                                <Card
                                    key={restaurant.id}
                                    className="overflow-hidden rounded-3xl transition-transform hover:translate-y-[-10px] hover:shadow-xl"
                                >
                                    <img
                                        src={`/placeholder.svg?height=200&width=400&text=${encodeURIComponent(restaurant.name)}`}
                                        alt={restaurant.name}
                                        className="h-48 w-full object-cover"
                                    />
                                    <CardContent className="p-6">
                                        <h2 className="text-xl font-bold mb-2">{restaurant.name}</h2>
                                        <span className="inline-block bg-[#ffe066] text-gray-800 px-3 py-1 rounded-full text-sm font-semibold mb-4">
                      {restaurant.category}
                    </span>
                                        <p className="text-gray-600 mb-4">
                                            <span className="font-medium">Location:</span> {restaurant.borough}
                                        </p>
                                        <a
                                            href={`https://www.google.com/search?q=${encodeURIComponent(restaurant.name + " " + restaurant.borough + " London")}`}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="inline-block text-[#ff6b6b] font-semibold py-2 px-4 border-2 border-[#ff6b6b] rounded-full transition-colors hover:bg-[#ff6b6b] hover:text-white"
                                        >
                                            Visit Website
                                        </a>
                                    </CardContent>
                                </Card>
                            ))
                        )}
                    </div>
                </div>
            </main>

            <footer className="bg-gray-800 text-white text-center py-6">
                <p>&copy; 2025 GoodEats</p>
            </footer>
        </div>
    )
}

