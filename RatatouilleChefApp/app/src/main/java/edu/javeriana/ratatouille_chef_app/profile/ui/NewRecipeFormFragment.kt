package edu.javeriana.ratatouille_chef_app.profile.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Ingredient
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import edu.javeriana.ratatouille_chef_app.profile.entities.Utensil
import edu.javeriana.ratatouille_chef_app.profile.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_new_recipe_form.*
import org.jetbrains.anko.onCheckedChange

class NewRecipeFormFragment : Fragment() {

    private var profileViewModel: ProfileViewModel? = null
    private val db = FirebaseFirestore.getInstance()
    private val selectedUtensil = mutableListOf<DocumentReference>()
    private val selectedIngredients = mutableListOf<DocumentReference>()


    private val ingredientsListObserver = Observer<List<Ingredient>> { ingredients ->
        ingredients.forEach {
            val chipItem = Chip(ingredientsChipGroup.context)
            chipItem.text = it.name
            chipItem.isCheckable = true
            chipItem.onCheckedChange { _, isChecked ->
                val ref = db.collection("ingredients").document(it.id)
                if (isChecked) selectedIngredients.add(ref)
                else selectedIngredients.remove(ref)
            }
            ingredientsChipGroup.addView(chipItem)
        }
    }

    private val utensilsListObserver = Observer<List<Utensil>> { utensil ->
        utensil.forEach {
            val chipItem = Chip(utensilsChipGroup.context)
            chipItem.text = it.name
            chipItem.isCheckable = true
            chipItem.onCheckedChange { _, isChecked ->
                val ref = db.collection("utensils").document(it.id)
                if (isChecked) selectedUtensil.add(ref)
                else selectedUtensil.remove(ref)

            }
            utensilsChipGroup.addView(chipItem)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_recipe_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        fetchViewControllers()
        fetchData()
        setupButtons()
    }

    private fun setupButtons() {
        createRecipe.setOnClickListener {
            val recipe = Recipe(
                name = nameEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                ingredients = selectedIngredients,
                utensils = selectedUtensil
            )
            Log.d("RECEPE", recipe.toString())
            profileViewModel?.createRecipe(recipe)
            view?.findNavController()?.navigate(R.id.action_newRecipeFormFragment_to_profileFragment)

        }
    }


    private fun fetchViewControllers() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    private fun fetchData() {
        profileViewModel?.ingredientsListLiveData?.observe(this, ingredientsListObserver)
        profileViewModel?.utensilsListLiveData?.observe(this, utensilsListObserver)
        profileViewModel?.findAllIngredients()
        profileViewModel?.findAllUtensils()

    }


}
