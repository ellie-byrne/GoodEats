import { NextResponse } from "next/server"
import { parse } from "csv-parse/sync"

interface CsvRestaurant {
    Borough: string
    Name: string
    Category: string
}

interface Restaurant {
    id: number
    borough: string
    name: string
    category: string
}

export async function GET() {
    try {
        // Fetch the CSV from the provided URL
        const csvUrl =
            "https://hebbkx1anhila5yf.public.blob.vercel-storage.com/5%20restaurants%20-%20Sheet1-92mmFnF0BQEwH65vyECSSKpYz0UyP0.csv"
        const response = await fetch(csvUrl)

        if (!response.ok) {
            throw new Error(`Failed to fetch CSV: ${response.status} ${response.statusText}`)
        }

        const csvContent = await response.text()

        // Parse the CSV content
        const records = parse(csvContent, {
            columns: true,
            skip_empty_lines: true,
        }) as CsvRestaurant[]

        console.log(`Parsed ${records.length} restaurants from CSV`)

        // Transform the data
        const restaurants: Restaurant[] = records.map((record, index) => ({
            id: index + 1,
            borough: record.Borough || "Unknown",
            name: record.Name || "Unnamed Restaurant",
            category: record.Category || "Restaurant",
        }))

        return NextResponse.json(restaurants)
    } catch (error) {
        console.error("Error fetching or parsing CSV:", error)
        return NextResponse.json({ error: "Failed to load restaurant data" }, { status: 500 })
    }
}

